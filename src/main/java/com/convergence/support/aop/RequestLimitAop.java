package com.convergence.support.aop;

import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.convergence.support.RequestLimit;
import com.convergence.support.RequestLimitException;
import com.convergence.support.util.IpUtil;
@Aspect
public class RequestLimitAop {
	private static final Logger logger = LoggerFactory.getLogger(RequestLimitAop.class);
	@Autowired
	private RedisTemplate redisTemplate;

	@Before("within(@org.springframework.stereotype.Controller *) && @annotation(limit)")
	public void requestLimit(JoinPoint joinPoint, RequestLimit limit) throws RequestLimitException {
		try {
			Object[] args = joinPoint.getArgs();
			HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
					.getRequest();
			String ipAddress = IpUtil.getIpAddress(request);
			String url = request.getRequestURL().toString();
			String key = "req_limit_".concat(url).concat("_").concat(ipAddress);
			boolean checkResult = checkWithRedis(limit, key);
			if (!checkResult) {
				logger.debug("requestLimited," + "[用户ip:{}],[访问地址:{}]超过了限定的次数[{}]次", ipAddress, url, limit.count());
				throw new RequestLimitException();
			}
		} catch (RequestLimitException e) {
			throw e;
		}

	}

	private boolean checkWithRedis(RequestLimit limit, String key) {
		long count = redisTemplate.opsForValue().increment(key, 1);
		if (count == 1) {
			redisTemplate.expire(key, limit.time(), TimeUnit.MILLISECONDS);
		}
		if (count > limit.count()) {
			return false;
		}
		return true;
	}
}
