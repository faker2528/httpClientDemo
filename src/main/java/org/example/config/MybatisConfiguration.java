package org.example.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MybatisConfiguration {

    /**
     * 可以添加其他插件
     * 自动分页: PaginationInnerInterceptor
     * 多租户: TenantLineInnerInterceptor
     * 动态表名: DynamicTableNameInnerInterceptor
     * 乐观锁: OptimisticLockerInnerInterceptor
     * SQL 性能规范: IllegalSQLInnerInterceptor
     * 防止全表更新与删除: BlockAttackInnerInterceptor

     * 使用多个插件时，需要注意它们的顺序。建议的顺序是：

     * 多租户、动态表名
     * 分页、乐观锁
     * SQL 性能规范、防止全表更新与删除
     * 总结：对 SQL 进行单次改造的插件应优先放入，不对 SQL 进行改造的插件最后放入。
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {

        // 初始化核心插件
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 可以添加其他插件
        // 比如说添加分页插件(DbType.MYSQL) 如果有多数据源可以不配数据库具体类型
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor(DbType.MYSQL);
        paginationInnerInterceptor.setMaxLimit(1000L); // 设置分页上限
        interceptor.addInnerInterceptor(paginationInnerInterceptor); // 将分页插件添加进核心插件中

        return interceptor;
    }
}
