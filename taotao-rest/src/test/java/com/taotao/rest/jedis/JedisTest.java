package com.taotao.rest.jedis;

import org.junit.Test;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class JedisTest {
	
	@Test
	public void testJedisSingle() {
		//创建一个jedis的对象。
		Jedis jedis = new Jedis("192.168.143.88", 6379);
		//调用jedis对象的方法，方法名称和redis的命令一致。
		jedis.set("key1", "jedis test");
		String string = jedis.get("key1");
		System.out.println(string);
		//关闭jedis。
		jedis.close();
	}


	/**
	 * test连接池
	 */
	public void testJedisPool(){
		//创建连接池
		JedisPool jedisPool = new JedisPool("192.168.143.88", 6379);
		//从连接池中获取jedis对象
		Jedis jedis = jedisPool.getResource();
		String string = jedis.get("key1");
		System.out.println(string);
		//关闭jedis对象
		jedis.close();
		jedisPool.close();

	}
}
