import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class Test01 {

    @Test
    public void v1(){
        ApplicationContext ac = new ClassPathXmlApplicationContext("applicationContext.xml");
        DriverManagerDataSource dataSource = (DriverManagerDataSource) ac.getBean("dataSource");
        System.out.println(dataSource.getUrl());
    }
}
