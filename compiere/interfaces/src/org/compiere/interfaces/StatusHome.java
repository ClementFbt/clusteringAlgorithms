/*
 * Generated by XDoclet - Do not edit!
 */
package org.compiere.interfaces;

/**
 * Home interface for compiere/Status.
 */
public interface StatusHome
   extends javax.ejb.EJBHome
{
   public static final String COMP_NAME="java:comp/env/ejb/compiere/Status";
   public static final String JNDI_NAME="compiere/Status";

   public org.compiere.interfaces.Status create()
      throws javax.ejb.CreateException,java.rmi.RemoteException;

}
