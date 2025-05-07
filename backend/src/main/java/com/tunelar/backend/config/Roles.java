package com.tunelar.backend.config;

/**
 * Defines user roles for Tunelar
 */
public class Roles {
	
	/** Admin role name */
	public static final String ROLE_ADMIN = "ROLE_ADMIN";
	
	/**
	 * Defines all roles in the system, EXCEPT for the Admin role.
	 */
	public enum UserRoles {
		
		/** Mod for Tunelar */
		ROLE_MOD,
		/** Prod for Tunelar */
		ROLE_PROD

	}
	
}