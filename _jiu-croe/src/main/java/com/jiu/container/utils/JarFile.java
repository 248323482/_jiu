package com.jiu.container.utils;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.shared.invoker.*;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @Author Administrator
 * @create 2020/8/7 10:08
 */
public class JarFile {
    private static final String USER_HOME = System.getProperty("user.home", "oms");
    private static final String COMMON_PATH = USER_HOME + "/powerjob-server/";
    // 部署间隔
    private static final long DEPLOY_MIN_INTERVAL = 10 * 60 * 1000;


    /**
     * 部署容器
     *
     * @param session WebSocket Session
     * @throws Exception 异常
     */
    public void deploy(Session session, Container container) throws Exception {

        RemoteEndpoint.Async remote =null;
        if(session!=null){ remote = session.getAsyncRemote();}
        try {

            Date lastDeployTime = container.getLastDeployTime();
            if (lastDeployTime != null) {
                if ((System.currentTimeMillis() - lastDeployTime.getTime()) < DEPLOY_MIN_INTERVAL) {
                    if(remote!=null) {
                        remote.sendText("SYSTEM: [warn] deploy too frequent, last deploy time is: " + new Date().getTime());
                    }
                }
            }
            // 准备文件
            File jarFile = prepareJarFile(container, session);
            if (jarFile == null) {
                return;
            }

            double sizeMB = 1.0 * jarFile.length() / FileUtils.ONE_MB;
            if(remote!=null) {
                remote.sendText(String.format("SYSTEM: the jarFile(size=%fMB) is prepared and ready to be deployed to the worker.", sizeMB));
            }
            // 修改数据库，更新 MD5和最新部署时间
            Date now = new Date();
            container.setGmtModified(now);
            container.setLastDeployTime(now);

            // 开始部署（需要分批进行,部署在不同服务器）
            Set<String> workerAddressList = new HashSet<>();
            if (workerAddressList.isEmpty()) {
                remote.sendText("SYSTEM: there is no worker available now, deploy failed!");
                return;
            }

            long sleepTime = calculateSleepTime(jarFile.length());

            if(remote!=null) {
                remote.sendText("SYSTEM: deploy finished, congratulations!");
            }

        } finally {
        }
    }

    //打包Jar包
    private File prepareJarFile(Container container, Session session) throws Exception {
        RemoteEndpoint.Async remote = null;

        if (session != null) {
            remote = session.getAsyncRemote();
        }

        // 获取Jar，Git需要先 clone成Jar计算MD5，JarFile则直接下载
        ContainerSourceType sourceType = ContainerSourceType.Git;
        if (sourceType == ContainerSourceType.Git) {

            String workerDirStr = this.genTemporaryWorkPath();
            //临时目录
            File workerDir = new File(workerDirStr);
            FileUtils.forceMkdir(workerDir);

            try {
                // git clone
                if (remote != null) {
                    remote.sendText("SYSTEM: start to git clone the code repo, using config: " + container.getSourceInfo());
                }
                GitRepository gitRepoInfo = JSON.parseObject(container.getSourceInfo(), GitRepository.class);
                CloneCommand cloneCommand = Git.cloneRepository()
                        .setDirectory(workerDir)
                        .setURI(gitRepoInfo.getRepositoryUrl())
                        .setBranch(gitRepoInfo.getBranch());
                if (!StringUtils.isEmpty(gitRepoInfo.getUsername())) {
                    CredentialsProvider credentialsProvider = new UsernamePasswordCredentialsProvider(gitRepoInfo.getUsername(), gitRepoInfo.getPassword());
                    cloneCommand.setCredentialsProvider(credentialsProvider);
                }
                cloneCommand.call();
                String oldVersion = container.getVersion();
                // 获取最新的 commitId 作为版本
                try (Repository repository = Git.open(workerDir).getRepository()) {
                    Ref head = repository.getRefDatabase().findRef("HEAD");
                    container.setVersion(head.getObjectId().getName());
                }

                if (container.getVersion().equals(oldVersion)) {
                    if (remote != null) {
                        remote.sendText(String.format("SYSTEM: this commitId(%s) is the same as the last.", oldVersion));
                    }
                } else {
                    if (remote != null) {
                        remote.sendText(String.format("SYSTEM: new version detected, from %s to %s.", oldVersion, container.getVersion()));
                    }
                }
                if (remote != null) {
                    remote.sendText("SYSTEM: git clone successfully, star to compile the project.");
                }

                // mvn clean package -DskipTests -U
                Invoker mvnInvoker = new DefaultInvoker();
                InvocationRequest ivkReq = new DefaultInvocationRequest();
                // -U：强制让Maven检查所有SNAPSHOT依赖更新，确保集成基于最新的状态
                // -e：如果构建出现异常，该参数能让Maven打印完整的stack trace
                // -B：让Maven使用批处理模式构建项目，能够避免一些需要人工参与交互而造成的挂起状态
                ivkReq.setGoals(Lists.newArrayList("clean", "package", "-DskipTests", "-U", "-e", "-B"));
                ivkReq.setBaseDirectory(workerDir);
                    ivkReq.setOutputHandler(remote!=null?remote::sendText:new InvocationOutputHandler() {
                        @Override
                        public void consumeLine(String s) throws IOException {
                            System.err.println(s);
                        }
                    });
                ivkReq.setBatchMode(true);

                mvnInvoker.execute(ivkReq);
                //打包成功目录
                String targetDirStr = workerDirStr + "/target";

                File targetDir = new File(targetDirStr);
                Collection<File> jarFile = FileUtils.listFiles(targetDir, FileFilterUtils.asFileFilter((dir, name) -> name.endsWith("jar-with-dependencies.jar")), null);

                if (CollectionUtils.isEmpty(jarFile)) {
                    if (remote != null) {
                        remote.sendText("SYSTEM: can't find packaged jar(maybe maven build failed), so deploy failed.");
                    }
                    return null;
                }

                File jarWithDependency = jarFile.iterator().next();

                // 将文件从临时工作目录移动到正式目录
                String jarFileName = genContainerJarName(container.getVersion());
                String localFileStr = this.genContainerJarPath() + jarFileName;
                File localFile = new File(localFileStr);
                if (localFile.exists()) {
                    FileUtils.forceDelete(localFile);
                }
                FileUtils.copyFile(jarWithDependency, localFile);

                return localFile;
            } finally {
                // 删除工作区数据
                FileUtils.forceDelete(workerDir);
            }
        }

        // 先查询本地是否存在目标 Jar 文件
        String jarFileName = genContainerJarName(container.getVersion());
        String localFileStr = this.genContainerJarPath() + jarFileName;
        File localFile = new File(localFileStr);
        if (localFile.exists()) {
            if (remote != null) {
                remote.sendText("SYSTEM: find the jar file in local disk.");
            }
            return localFile;
        }

        // 从 MongoDB 下载
        if (remote != null) {
            remote.sendText(String.format("SYSTEM: try to find the jarFile(%s) in GridFS", jarFileName));
        }
        downloadJarFrom(jarFileName, localFile);
        if (remote != null) {
            remote.sendText("SYSTEM: download jar file from GridFS successfully~");
        }
        return localFile;
    }


    //其他地方下载jar
    private void downloadJarFrom(String jarFileName, File localFile) {
    }

    /**
     * 获取临时目录（随机目录，不会重复），用完记得删除
     *
     * @return 临时目录
     */
    public static String genTemporaryWorkPath() {
        return genTemporaryPath() + new Date().getTime() + "/";
    }


    private static String genContainerJarName(String version) {
        return String.format(new Date().getTime() + "-%s.jar", version);
    }


    /**
     * 获取临时目录（固定目录）
     *
     * @return 目录
     */
    public static String genTemporaryPath() {
        return COMMON_PATH + "temp/";
    }

    /**
     * 获取用于构建容器的 jar 文件存放路径
     *
     * @return 路径
     */
    public static String genContainerJarPath() {
        return COMMON_PATH + "container/";
    }

    /**
     * 计算 sleep 时间（每10M睡眠1S + 1）
     *
     * @param fileLength 文件的字节数
     * @return sleep 时间
     */
    private long calculateSleepTime(long fileLength) {
        return (fileLength / FileUtils.ONE_MB / 10 + 1) * 1000;
    }

    @Data
    public class GitRepository {
        // 仓库地址
        private String repositoryUrl;
        // 分支名称
        private String branch;
        // 用户名
        private String username;
        // 密码
        private String password;
    }

    @Data
    public class Container {

        private Long id;

        // 所属的应用ID
        private Long appId;

        private String containerName;

        // 容器类型，枚举值为 ContainerSourceType
        private Integer sourceType;
        // 由 sourceType 决定，JarFile -> String，存储文件名称；Git -> JSON，包括 URL，branch，username，password
        private String sourceInfo;

        // 版本 （Jar包使用md5，Git使用commitId，前者32位，后者40位，不会产生碰撞）
        private String version;

        // 状态，枚举值为 ContainerStatus
        private Integer status;

        // 上一次部署时间
        private Date lastDeployTime;

        private Date gmtCreate;
        private Date gmtModified;
    }

    @Getter
    @AllArgsConstructor
    public enum ContainerSourceType {

        FatJar(1, "Jar文件"),
        Git(2, "Git代码库");

        private final int v;
        private final String des;

        public static ContainerSourceType of(int v) {
            for (ContainerSourceType type : values()) {
                if (type.v == v) {
                    return type;
                }
            }
            throw new IllegalArgumentException("unknown ContainerSourceType of " + v);
        }

    }
}
