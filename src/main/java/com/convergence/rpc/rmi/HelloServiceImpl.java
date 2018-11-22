package com.convergence.rpc.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class HelloServiceImpl extends UnicastRemoteObject implements HelloService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected HelloServiceImpl() throws RemoteException {
		super();
	}

	@Override
	public String sayHello(String content) throws RemoteException {
		return "hello" + content;
	}

}
