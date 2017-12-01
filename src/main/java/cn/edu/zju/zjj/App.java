package cn.edu.zju.zjj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Hello world!
 *
 */
@SpringBootApplication
@RestController
public class App
{
    @RequestMapping(value = "/home", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public @ResponseBody Object home(){
        return "welcome";
    }
    public static void main( String[] args )
    {
        SpringApplication.run(App.class, args);
    }
}
