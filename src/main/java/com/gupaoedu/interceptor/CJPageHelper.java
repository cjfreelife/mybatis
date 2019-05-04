package com.gupaoedu.interceptor;

import org.apache.ibatis.builder.StaticSqlSource;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.lang.reflect.Field;
import java.util.Properties;

@Intercepts({@Signature(type = Executor.class, method = "query",
        args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})
})
public class CJPageHelper implements Interceptor {
    public Object intercept(Invocation invocation) throws Throwable {
        System.out.println(invocation);
        Object[] args = invocation.getArgs();
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        BoundSql boundSql = mappedStatement.getBoundSql(args[1]);
        String sql = boundSql.getSql();
        RowBounds rowBounds = (RowBounds) args[2];
        if (rowBounds != null) {
            int limit = rowBounds.getLimit();
            int offset = rowBounds.getOffset();
//            sql = sql + " limit " + limit + " " + offset;
            sql = sql + String.format(" limit %d , %d", offset, limit);
        }
        StaticSqlSource staticSqlSource = new StaticSqlSource(mappedStatement.getConfiguration(), sql);
        Field sqlSource = MappedStatement.class.getDeclaredField("sqlSource");
        sqlSource.setAccessible(true);
        sqlSource.set(mappedStatement, staticSqlSource);
        return invocation.proceed();
    }

    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    public void setProperties(Properties properties) {

    }
}
