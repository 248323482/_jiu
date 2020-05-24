// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   MapWrapper.java

package com.jiu.wallet.btc.rpc;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

class MapWrapper
{

	public final Map m;

	public MapWrapper(Map m)
	{
		this.m = m;
	}

	public boolean mapBool(String key)
	{
		return mapBool(m, key);
	}

	public float mapFloat(String key)
	{
		return mapFloat(m, key);
	}

	public double mapDouble(String key)
	{
		return mapDouble(m, key);
	}

	public int mapInt(String key)
	{
		return mapInt(m, key);
	}

	public long mapLong(String key)
	{
		return mapLong(m, key);
	}

	public String mapStr(String key)
	{
		return mapStr(m, key);
	}

	public Date mapCTime(String key)
	{
		return mapCTime(m, key);
	}

	public static boolean mapBool(Map m, String key)
	{
		return ((Boolean)m.get(key)).booleanValue();
	}

	public static float mapFloat(Map m, String key)
	{
		return ((Number)m.get(key)).floatValue();
	}

	public static double mapDouble(Map m, String key)
	{
		return ((Number)m.get(key)).doubleValue();
	}

	public static BigDecimal mapBigDecimal(Map m, String key)
	{
		return new BigDecimal(m.get(key).toString());
	}

	public static int mapInt(Map m, String key)
	{
		return ((Number)m.get(key)).intValue();
	}

	public static long mapLong(Map m, String key)
	{
		return ((Number)m.get(key)).longValue();
	}

	public static String mapStr(Map m, String key)
	{
		Object v = m.get(key);
		return v != null ? String.valueOf(v) : null;
	}

	public static Date mapCTime(Map m, String key)
	{
		Object v = m.get(key);
		return v != null ? new Date(mapLong(m, key) * 1000L) : null;
	}

	public String toString()
	{
		return String.valueOf(m);
	}
}
