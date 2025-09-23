package com.loopers.support.cache.optimized

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.stereotype.Component

@Aspect
@Component
class OptimizedCacheAspect(
    private val optimizedCacheManager: OptimizedCacheManager,
) {

    @Around("@annotation(OptimizedCacheable)")
    @Throws(Throwable::class)
    fun around(joinPoint: ProceedingJoinPoint): Any? {
        val cacheable = findAnnotation(joinPoint)
        return optimizedCacheManager.process(
            cacheable.type,
            cacheable.ttlSeconds,
            joinPoint.args,
            findReturnType(joinPoint)!!,
            {
                try {
                    joinPoint.proceed()
                } catch (throwable: Throwable) {
                    throw RuntimeException(throwable)
                }
            },
        )
    }

    private fun findAnnotation(joinPoint: ProceedingJoinPoint): OptimizedCacheable {
        return (joinPoint.signature as MethodSignature).method.getAnnotation(OptimizedCacheable::class.java)
    }

    private fun findReturnType(joinPoint: ProceedingJoinPoint): Class<*>? {
        return (joinPoint.signature as MethodSignature).returnType
    }
}
