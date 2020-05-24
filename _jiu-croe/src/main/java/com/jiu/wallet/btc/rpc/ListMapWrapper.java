// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ListMapWrapper.java

package com.jiu.wallet.btc.rpc;

import java.util.*;

abstract class ListMapWrapper extends AbstractList
{

	public final List list;

	public ListMapWrapper(List list)
	{
		this.list = list;
	}

	protected abstract Object wrap(Map map);

	public Object get(int index)
	{
		return wrap((Map)list.get(index));
	}

	public int size()
	{
		return list.size();
	}
}
