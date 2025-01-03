package org.stlgaa.personalization;

import jakarta.annotation.Resource;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.jms.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.stlgaa.SLGConstraintException;
import org.stlgaa.SLGService;
import org.stlgaa.security.authentication.AuthenticationService;

import java.util.List;
import java.util.UUID;

/**
 * API and business logic for CRUD operations on User objects and related personalization functionality.
 */
@JMSDestinationDefinitions({
        @JMSDestinationDefinition(
                name = "java:/jms/topics/UserCreatedTopic",
                interfaceName = "jakarta.jms.Topic",
                destinationName = "UserCreatedTopic"
        ),
        @JMSDestinationDefinition(
                name = "java:/jms/topics/UserUpdatedTopic",
                interfaceName = "jakarta.jms.Topic",
                destinationName = "UserUpdatedTopic"
        ),
        @JMSDestinationDefinition(
                name = "java:/jms/topics/UserDeletedTopic",
                interfaceName = "jakarta.jms.Topic",
                destinationName = "UserDeletedTopic"
        )
})
@Stateless
public class UserService extends SLGService {

    @PersistenceContext(unitName = "stlgaa-persistence")
    private EntityManager entityManager;

    @EJB
    private AuthenticationService authenticationService;

    @Inject
    private JMSContext jmsContext;

    @Resource(lookup = "java:/jms/topics/UserCreatedTopic")
    private Topic userCreatedTopic;

    @Resource(lookup = "java:/jms/topics/UserUpdatedTopic")
    private Topic userUpdatedTopic;

    @Resource(lookup = "java:/jms/topics/UserDeletedTopic")
    private Topic userDeletedTopic;

    /**
     * <p>Create a new User. A user created this way will not have any credentials and therefore will not be able to login.
     * They will be able to use Forgot Password functionality to set their credentials, as that functionality will create
     * a new PasswordCredential if one does not exist for this User. An administrator will also be able to set a password
     * for this User.</p>
     *
     * <p>A message will be published to the Topic "UserCreatedTopic".</p>
     *
     * @param user a User with required fields to be created
     * @throws SLGConstraintException if a User with the provided e-mail address already exists
     */
    public void createUser(User user) throws SLGConstraintException {

        /* Ensure unique e-mail address */
        User existingUser = getUserByEmail(user.getEmailAddress());
        if (existingUser != null) {
            log.debug("Attempted to create user with email address {} when one already exists.", user.getEmailAddress().getAddress());
            throw new SLGConstraintException("User with email address " + user.getEmailAddress().getAddress() + " already exists.");
        }

        /* Represent phone number as simple string of _only_ digits for now, strip non-numeric characters */
        if (user.getPhoneNumber() != null && !user.getPhoneNumber().isBlank()) {
            user.setPhoneNumber(user.getPhoneNumber().replaceAll("\\D", ""));
        } else {
            user.setPhoneNumber("");
        }

        entityManager.persist(user);

        /* Publish User created event */
        try {

            ObjectMessage jmsMessage = jmsContext.createObjectMessage();
            jmsMessage.setObject(user);
            jmsContext.createProducer().send(userCreatedTopic, jmsMessage);

        } catch (JMSException e) {
            log.error("Failed to send JMS message for user created event. Subscribers may not function.");
            log.catching(e);
        }
    }

    public void createUserWithPassword(User user, String password) throws SLGConstraintException {
        createUser(user);
        authenticationService.createPassword(user, password);
    }

    /**
     * Gets a User by ID.
     *
     * @param id a UUID corresponding to a User
     * @return a User with the corresponding ID or null if one does not exist
     */
    public User getUserById(UUID id) {
        return entityManager.find(User.class, id);
    }

    /**
     * Gets a User by e-mail address.
     *
     * @param emailAddress a jakarta.mail.InternetAddress corresponding to a User
     * @return the User with the supplied e-mail address, or null of none exists
     */
    public User getUserByEmail(InternetAddress emailAddress) {

        TypedQuery<User> query = entityManager.createNamedQuery(User.GET_BY_EMAIL, User.class);
        query.setParameter("emailAddress", emailAddress);

        List<User> results = query.getResultList();
        if (results.isEmpty()) {
            return null;
        } else if (results.size() > 1) {
            log.warn("More than one user found for email address {}. Application in illegal state, unexpected behavior may occur.", emailAddress);
        }

        return results.get(0);
    }

    /**
     * <p>Updates a User.</p>
     *
     * <p>Checks to ensure the User exists already.</p>
     *
     * @param user a User with updated properties
     * @return the newly updated User
     * @throws SLGConstraintException if the User does not exist, or if the update breaks a unique constraint
     */
    public User updateUser(User user) throws SLGConstraintException {

        /* Is this an existing user? If not, should be using the createUser method to apply proper logic */
        if (user.getId() == null) {
            log.debug("Attempted to update user with null id.");
            throw new SLGConstraintException("User with id " + user.getId() + " does not exist. Please use the createUser service to create a new User.");
        }

        /* Get the database record to see what is being updated and whether special logic should be applied */
        User existingRecord = entityManager.find(User.class, user.getId());
        if (existingRecord == null) {
            log.debug("Attempted to update user with id {} when one does not exist.", user.getId());
            throw new SLGConstraintException("User with id " + user.getId() + " does not exist. The User has been deleted or the ID is made up or otherwise invalid.");
        }

        /* If email address is being updated, ensure it is unique */
        if (!user.getEmailAddress().equals(existingRecord.getEmailAddress())) {
            User userWithEmail = getUserByEmail(user.getEmailAddress());
            if (userWithEmail != null) {
                throw new SLGConstraintException("User with email address " + user.getEmailAddress().getAddress() + " already exists. Cannot update user " + user.getId() + " with this email.");
            }
        }

        User mergedUser = entityManager.merge(user);

        /* Publish User updated event */
        try {

            ObjectMessage jmsMessage = jmsContext.createObjectMessage();
            jmsMessage.setObject(mergedUser);
            jmsContext.createProducer().send(userUpdatedTopic, jmsMessage);

        } catch (JMSException e) {
            log.error("Failed to send JMS message for user updated event. Subscribers may not function.");
            log.catching(e);
        }

        return mergedUser;
    }

    /**
     * <p>Deletes a User from the system.</p>
     *
     * <p>Publishes the UUID of the deleted User to UserDeletedTopic.</p>
     *
     * @param id the UUID of the User to be deleted
     */
    public void deleteUserById(UUID id) {

        User managedUser = getUserById(id);
        if (managedUser != null) {

            entityManager.remove(managedUser);

            /* Publish User deleted event */
            Message jmsMessage = jmsContext.createTextMessage(id.toString());
            jmsContext.createProducer().send(userDeletedTopic, jmsMessage);

        }
    }

    /**
     * Deletes a User from the system.
     *
     * @param user the User to be deleted.
     */
    public void deleteUser(User user) {
        deleteUserById(user.getId());
    }
}
