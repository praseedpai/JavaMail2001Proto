package com.jthomas.pop;

/*
 * apop.java
 * Copyright (c) 1996 John Thomas  jthomas@cruzio.com
 *      All Rights Reserved.
 *
 * Permission to use, copy, modify, and distribute this software and
 * its documentation for commercial or non-commercial purposes 
 * is hereby granted provided that this copyright notice
 * appears in all copies.
 *
 * LIMITATION OF LIABILITY.  UNDER NO CIRCUMSTANCES AND UNDER NO
 * LEGAL THEORY, SHALL THE AUTHOR OF THIS CLASS BE LIABLE TO YOU
 * OR ANY OTHER PERSON FOR ANY INDIRECT, SPECIAL, INCIDENTAL OR
 * CONSEQUENTIAL DAMAGES OF ANY KIND.
 *
 */

import java.io.*;
import java.util.*;
import java.net.*;
import java.security.*;
 
/** 
 * <p>Get the latest version of this and other classes on
 * <a href="http://www.geocities.com/SunsetStrip/Studio/4994/java.html">
 * Stefano Locati's Java page.</a>
 *
 * <p>Interface to a POP3 Mail server. This class is smart enough to
 * use APOP authorization only if the server sends a timestamp in
 * the initial greeting. (This means it supports APOP login).
 * However, if APOP login fails tries to log on in the normal way.
 * Based on the 
 * <a href="http://ds.internic.net/rfc/rfc1939.txt">rfc1939.txt</a>
 * definition for the POP 3 Mail
 * client/server protocol.
 * <b>If mailhost, user, password are not supplied on constructor
 * then they must be specified on the connect and login calls.</b>
 *
 * @author <b>Original author:</b> John Thomas
 *  <a href="mailto:jthomas@cruzio.com">jthomas@cruzio.com</a>
 * @author <b>Current maintainer:</b> Stefano Locati 
 *  <a href="mailto:slocati@geocities.com">slocati@geocities.com</a> or
 *  <a href="mailto:stefano.locati@usa.net">stefano.locati@usa.net</a>
 * @version $Revision: 1.9 $ $Date: 1998/09/02 08:05:18 $
 * @see pop3
 */

public class apop extends pop3   {


    /** Timestamp from connection msg that will be used by APOP */
    String ConnectTimestamp = null;
    
  
    /**
     *  This will just create the object. No work is done.
     *  @param host      POP3 server host name
     *  @param user      mailbox user name
     *  @param password  mailbox password
     */
    public apop(String host, String user, String password) {
	super(host, user, password);
    }

    /**
     *  This will just create the object. No work is done.
     *  You will have to supply host, user and password 
     *  through connect() and login() methods. 
     */    
    public apop() {
	super();
    }
    
    
    //----------------------------------------------------------

    /**
     *  APOP override for connect method.<br>
     *  - Calls the connect method for pop3 class.<br>
     *  - Searches response for additional info.
     *    It should contain the "nonce" that will be used in calculating
     *    the MD5 message digest.
     *  @return popStatus:  result of this operation
     */
    public synchronized popStatus connect() {
	popStatus status;
	
        // without this, reusing an apop object would keep the timestamp
        // of the previous server when the second server doesn't have
        // apop capabilities. -- slocati
        ConnectTimestamp = null; 
	status = super.connect();
	if (status._OK) {
	    String[] tokens = Parse(status,-1);
	    // check the tokens for a timestamp in form <xxxxxxx>
	    // that will be used by the APOP command
	    for (int i=0; i<tokens.length; i++) {
		//debug("Token " + i + "= '" + tokens[i] + "'");
		if ( tokens[i] != null &&
		     tokens[i].charAt(0) == '<' &&
		     tokens[i].charAt(tokens[i].length()-1) == '>') {
		    ConnectTimestamp = tokens[i];
		    debug("APOP timestamp='" + ConnectTimestamp +"'");
		}
	    }
	}
	return status;
    }

    //----------------------------------------------------------

    /**
     *  Login the specified user using the APOP interface.
     *  @param user        mailbox user name
     *  @param password    mailbox password
     *  @return popStatus:  result of this operation
     */
    public popStatus login(String user, String password) {
	User = user;
	Password = password;
	return login();
    }

    //----------------------------------------------------------

    /**
     *  Login the specified user using the APOP interface.
     *  Username and password must have been provided in the
     *  constructor.
     *  @return popStatus:  result of this operation
     */
    public synchronized popStatus login() {
	popStatus status = new popStatus();

        if ( User == null || Password == null )
	    status._Response = "-ERR User or Password not specified";
	else {
	    if ( server == null ) status._Response = "-ERR Not connected";
            else status._OK = true;
        }
	if ( ConnectTimestamp == null ) {
	    // status._Response = "-ERR No Timestamp sent by Server on Connect";
            if ( status._OK ) status = super.login();
            return status;
        }
	// Everything looks good so we will concoct a message digest
	// from the Timestamp sent by the server at connect time
	// concatenated with the password.
	if ( status._OK ) {
	    String input = ConnectTimestamp + Password;
	    debug("MD5 input='" + input + "'");
            java.security.MessageDigest md5;
            String output = null;
            try {
                md5 = java.security.MessageDigest.getInstance("MD5");
                output = Convert.toHexString( md5.digest(input.getBytes()) );
            } catch (NoSuchAlgorithmException e) { }
	    debug("MD5 output='" + output + "'");	
	    send("APOP " + User + " " + output );
	    // send("LOGIN " + User );
	    status._Response = recv();
	    Parse(status,1);
	    if (status._OK) {
		State = TRANSACTION;
		// Now we will do an internal STAT function
		popStatus statStatus = stat();
	    } else { // never give up
                status = super.login();
            }
	}
	return status;
    }
    
    //----------------------------------------------------------

    /**
     *  Indicates if the Server will support APOP Login.
     *  We must have found a timestamp in the Connect banner.
     *  Generally this method is not needed, since this class is
     *  smart enough to decide between APOP and normal login.
     *  @return true if the server supports APOP login, false
     *    otherwise.
     */
    public boolean apopSupport() {
	return  ( ConnectTimestamp != null );
    }
    //-------------------------------------------------------

} // end of Class apop


