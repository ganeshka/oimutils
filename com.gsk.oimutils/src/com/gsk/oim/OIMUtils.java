package com.gsk.oim;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import javax.security.auth.login.LoginException;

import oracle.iam.identity.exception.NoSuchUserException;
import oracle.iam.identity.exception.UserAlreadyExistsException;
import oracle.iam.identity.exception.UserCreateException;
import oracle.iam.identity.exception.UserLockException;
import oracle.iam.identity.exception.UserLookupException;
import oracle.iam.identity.exception.UserModifyException;
import oracle.iam.identity.exception.UserSearchException;
import oracle.iam.identity.exception.UserUnlockException;
import oracle.iam.identity.exception.ValidationFailedException;
import oracle.iam.identity.usermgmt.api.UserManager;
import oracle.iam.identity.usermgmt.vo.User;
import oracle.iam.identity.usermgmt.vo.UserManagerResult;
import oracle.iam.platform.OIMClient;
import oracle.iam.platform.authz.exception.AccessDeniedException;
import oracle.iam.platform.entitymgr.vo.SearchCriteria;

/**
 * @author ganesh
 */
public class OIMUtils {

	private static final String USERS_ROLE = "Role";
	private static final String USERS_PASSWORD = "usr_password";
	private static final String USERS_EMAIL = "Email";
	private static final String USERS_LAST_NAME = "Last Name";
	private static final String USERS_FIRST_NAME = "First Name";
	private static final String USERS_ACT_KEY = "act_key";
	private static final String USERS_LOGIN = "User Login";
	private static final String JAVA_SECURITY_AUTH_LOGIN_CONFIG = "java.security.auth.login.config";
	private static final String APPSERVER_TYPE = "APPSERVER_TYPE";
	private static final String OIM_APP_SERVER_TYPE = "OIM.AppServerType";
	private static final String WLS = "wls";
	private UserManager userManager = null;
	private OIMClient oimClient = null;

	public OIMUtils() {

		initialize();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		OIMUtils oimUtils = new OIMUtils();
		oimUtils.createUser("Ganesh");
		oimUtils.inquireUser("Ganesh");
		oimUtils.updateUser("Ganesh");
		oimUtils.searchUser("ganesh.kamble@gmail.com");
		oimUtils.lockAccount("Ganesh");
		oimUtils.unlockAccount("Ganesh");
	}

	/**
	 * 
	 */
	private void initialize() {

		Hashtable<Object, Object> env = new Hashtable<Object, Object>();
		env.put(OIMClient.JAVA_NAMING_FACTORY_INITIAL, "weblogic.jndi.WLInitialContextFactory");
		env.put(OIMClient.JAVA_NAMING_PROVIDER_URL, "t3://localhost:14000");
		System.setProperty(JAVA_SECURITY_AUTH_LOGIN_CONFIG, "/home/ganesh/com.gsk.oimutils/config/authwl.conf");
		System.setProperty(OIM_APP_SERVER_TYPE, WLS);
		System.setProperty(APPSERVER_TYPE, WLS);
		oimClient = new OIMClient(env);
		try {
			oimClient.login("xelsysadm", "Welcome1".toCharArray(), env);
			userManager = oimClient.getService(UserManager.class);
		} catch (LoginException e2) {
		}
	}

	/**
	 * 
	 */
	public void createUser(String userId) {

		HashMap<String, Object> userAttributeValueMap = new HashMap<String, Object>();
		userAttributeValueMap.put(USERS_ACT_KEY, new Long(1));
		userAttributeValueMap.put(USERS_LOGIN, userId);
		userAttributeValueMap.put(USERS_FIRST_NAME, "Ganesh");
		userAttributeValueMap.put(USERS_LAST_NAME, "Kamble");
		userAttributeValueMap.put(USERS_EMAIL, "ganesh.kamble@hotmail.com");
		userAttributeValueMap.put(USERS_PASSWORD, "P1ssword");
		userAttributeValueMap.put(USERS_ROLE, "OTHER");
		User user = new User("Ganesh", userAttributeValueMap);
		try {
			UserManagerResult result = userManager.create(user);
			System.out.println(result.getStatus());
		} catch (ValidationFailedException e) {
			e.printStackTrace();
		} catch (UserAlreadyExistsException e) {
			e.printStackTrace();
		} catch (UserCreateException e) {
			e.printStackTrace();
		} catch (AccessDeniedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return
	 */
	public User inquireUser(String userName) {

		Set<String> resAttrs = new HashSet<String>();
		User user = null;
		try {
			user = userManager.getDetails(userName, resAttrs, true);
		} catch (NoSuchUserException e) {
			e.printStackTrace();
		} catch (UserLookupException e) {
			e.printStackTrace();
		} catch (AccessDeniedException e) {
			e.printStackTrace();
		}
		return user;
	}

	/**
	 * 
	 */
	public void updateUser(String userId) {

		HashMap<String, Object> userAttributeValueMap = new HashMap<String, Object>();
		userAttributeValueMap.put(USERS_ACT_KEY, new Long(1));
		userAttributeValueMap.put(USERS_LOGIN, userId);
		userAttributeValueMap.put(USERS_FIRST_NAME, "Ganesh");
		userAttributeValueMap.put(USERS_LAST_NAME, "Kamble");
		userAttributeValueMap.put(USERS_EMAIL, "ganesh.kamble@hotmail.com");
		userAttributeValueMap.put(USERS_PASSWORD, "P@ssword");
		userAttributeValueMap.put(USERS_ROLE, "Other");
		User retrievedUser = inquireUser("Ganesh");
		User user = new User((String) retrievedUser.getAttribute("User Login"), userAttributeValueMap);
		try {
			userManager.modify(user);
		} catch (ValidationFailedException e) {
			e.printStackTrace();
		} catch (UserModifyException e) {
			e.printStackTrace();
		} catch (NoSuchUserException e) {
			e.printStackTrace();
		} catch (AccessDeniedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	public List<User> searchUser(String emailId) {

		SearchCriteria searchCriteria = new SearchCriteria("Email", emailId, SearchCriteria.Operator.EQUAL);
		HashMap<String, Object> mapParams = new HashMap<String, Object>();
		Set<String> attrNames = null;
		mapParams.put("STARTROW", 0);
		mapParams.put("ENDROW", 1);
		List<User> users = null;
		try {
			users = userManager.search(searchCriteria, attrNames, mapParams);
		} catch (UserSearchException e) {
			e.printStackTrace();
		} catch (AccessDeniedException e) {
			e.printStackTrace();
		}
		return users;
	}

	/**
	 * @param userId
	 */
	public void lockAccount(String userId) {

		try {
			userManager.lock(userId, true, true);
		} catch (ValidationFailedException e) {
			e.printStackTrace();
		} catch (UserLockException e) {
			e.printStackTrace();
		} catch (NoSuchUserException e) {
			e.printStackTrace();
		} catch (AccessDeniedException e) {
			e.printStackTrace();
		}
	}

	public void unlockAccount(String userId) {

		try {
			userManager.unlock(userId, true);
		} catch (ValidationFailedException e) {
			e.printStackTrace();
		} catch (UserUnlockException e) {
			e.printStackTrace();
		} catch (NoSuchUserException e) {
			e.printStackTrace();
		} catch (AccessDeniedException e) {
			e.printStackTrace();
		}
	}
}
