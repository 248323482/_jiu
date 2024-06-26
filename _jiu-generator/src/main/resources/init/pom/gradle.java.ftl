mainClassName = "com.jiu.${service}ServerApplication"
dependencies{
compile project(':_jiu-croe')
compile ('org.springframework.boot:spring-boot-starter-thymeleaf')
compile group: 'com.aliyun.oss', name: 'aliyun-sdk-oss', version: '3.8.1'
compile group: 'com.github.tobato', name: 'fastdfs-client', version: '1.26.6'
compile group: 'com.qiniu', name: 'qiniu-java-sdk', version: '7.2.28'
compile("com.baomidou:mybatis-plus:3.3.1")
compile group: 'com.github.whvcse', name: 'easy-captcha', version: '1.6.2'
compile group: 'eu.bitwalker', name: 'UserAgentUtils', version: '1.21'
//替代tomcat
compile "org.springframework.boot:spring-boot-starter-undertow"
}