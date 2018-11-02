def gitRepo = "git@github.com:Dataman-Cloud/skate.git"
publicRegistryUrl = "demoregistry.dataman-inc.com"
publicImagePrefix = "${publicRegistryUrl}/skate"
publicRegistryUsername = "guangzhou"
publicRegistryPassword = "Gz-regsistry-2017@"
workRootDir = "/home/apps/jenkins-home/workspace/skate"
publicRepositoryId = "releases-public"
workRootDir = "/home/apps/jenkins-home/workspace/skate"

targetdockerfile = "target/"
sourcedockerfile = "src/main/docker"

node("master") {
    stage("Prepare") {
        sh "echo version is ${VERSION}, IS_PUSH is ${IS_PUSH}"

        git branch: "master", url: "${gitRepo}"
        sh "git pull origin dev"

				sh "echo 'execute replaceVersion'"
        replaceVersion()
    }

    stage("Release-Build") {
        //sh "mvn -DskipTests clean package"
        sh "mvn -DskipTests clean package -Dspring.profiles.active=docker"
    }

    stage("cp-dockerfile") {
    	sh "cp config-service/${sourcedockerfile}/Dockerfile  config-service/${targetdockerfile}"
    	sh "cp discovery-service/${sourcedockerfile}/Dockerfile  discovery-service/${targetdockerfile}"
    	sh "cp edge-service/${sourcedockerfile}/Dockerfile  edge-service/${targetdockerfile}"
    	sh "cp user-service/${sourcedockerfile}/Dockerfile  user-service/${targetdockerfile}"
    	sh "cp account-service/${sourcedockerfile}/Dockerfile  account-service/${targetdockerfile}"
    	sh "cp shopping-cart-service/${sourcedockerfile}/Dockerfile  shopping-cart-service/${targetdockerfile}"
    	sh "cp catalog-service/${sourcedockerfile}/Dockerfile  catalog-service/${targetdockerfile}"
    	sh "cp inventory-service/${sourcedockerfile}/Dockerfile  inventory-service/${targetdockerfile}"
    	sh "cp order-service/${sourcedockerfile}/Dockerfile  order-service/${targetdockerfile}"
    	sh "cp online-store-web/${sourcedockerfile}/Dockerfile  online-store-web/${targetdockerfile}"
    	sh "cp hystrix-dashboard/${sourcedockerfile}/Dockerfile hystrix-dashboard/${targetdockerfile}"
    }


    //调整到push tag 后，确保推内网所有操作成功
    //推公网image仓库
    pushImageToPublicRegistry()

    stage("Cleanup") {
        //恢复重置
        sh "echo 'Reset the version to master-SNAPSHOT'"
        sh "git reset --hard"
    }
}


/** 推images到公网的仓库**/
def pushImageToPublicRegistry() {

    if (params.IS_PUSH == "Yes") {

        stage("Push-image-config") {
            sh "docker build -t ${publicImagePrefix}/config-service:${VERSION} config-service/${targetdockerfile}"
            sh "docker login -u ${publicRegistryUsername} -p ${publicRegistryPassword} ${publicRegistryUrl}/${targetdockerfile}"
            sh "docker push ${publicImagePrefix}/config-service:${VERSION}"
        }

        stage("Push-image-discovery") {
            sh "docker build -t ${publicImagePrefix}/discovery-service:${VERSION} discovery-service/${targetdockerfile}"
            sh "docker login -u ${publicRegistryUsername} -p ${publicRegistryPassword} ${publicRegistryUrl}/${targetdockerfile}"
            sh "docker push ${publicImagePrefix}/discovery-service:${VERSION}"
        }

        stage("Push-image-edge") {
            sh "docker build -t ${publicImagePrefix}/edge-service:${VERSION} edge-service/${targetdockerfile}"
            sh "docker login -u ${publicRegistryUsername} -p ${publicRegistryPassword} ${publicRegistryUrl}/${targetdockerfile}"
            sh "docker push ${publicImagePrefix}/edge-service:${VERSION}"
        }

        stage("Push-image-biz") {
            sh "docker build -t ${publicImagePrefix}/user-service:${VERSION} user-service/${targetdockerfile}"
            sh "docker build -t ${publicImagePrefix}/account-service:${VERSION} account-service/${targetdockerfile}"
            sh "docker build -t ${publicImagePrefix}/shopping-cart-service:${VERSION} shopping-cart-service/${targetdockerfile}"
            sh "docker build -t ${publicImagePrefix}/catalog-service:${VERSION} catalog-service/${targetdockerfile}"
            sh "docker build -t ${publicImagePrefix}/inventory-service:${VERSION} inventory-service/${targetdockerfile}"
            sh "docker build -t ${publicImagePrefix}/order-service:${VERSION} order-service/${targetdockerfile}"
            sh "docker build -t ${publicImagePrefix}/online-store-web:${VERSION} online-store-web/${targetdockerfile}"
            sh "docker build -t ${publicImagePrefix}/hystrix-dashboard:${VERSION} hystrix-dashboard/${targetdockerfile}"

            sh "docker login -u ${publicRegistryUsername} -p ${publicRegistryPassword} ${publicRegistryUrl}"

            sh "docker push ${publicImagePrefix}/user-service:${VERSION}"
            sh "docker push ${publicImagePrefix}/account-service:${VERSION}"
            sh "docker push ${publicImagePrefix}/shopping-cart-service:${VERSION}"
            sh "docker push ${publicImagePrefix}/catalog-service:${VERSION}"
            sh "docker push ${publicImagePrefix}/inventory-service:${VERSION}"
            sh "docker push ${publicImagePrefix}/order-service:${VERSION}"
            sh "docker push ${publicImagePrefix}/online-store-web:${VERSION}"
            sh "docker push ${publicImagePrefix}/hystrix-dashboard:${VERSION}"
        }
    }
}

/** 替换版本号*/
def replaceVersion() {
    sh "echo Replace master-SNAPSHOT to ${VERSION} in pom.xml"
    sh "sed -i 's|master-SNAPSHOT|${VERSION}|g\' pom.xml"
    sh "sed -i 's|master-SNAPSHOT|${VERSION}|g\' config-service/pom.xml"
    sh "sed -i 's|master-SNAPSHOT|${VERSION}|g\' discovery-service/pom.xml"
    sh "sed -i 's|master-SNAPSHOT|${VERSION}|g\' hystrix-dashboard/pom.xml"
    sh "sed -i 's|master-SNAPSHOT|${VERSION}|g\' edge-service/pom.xml"
    sh "sed -i 's|master-SNAPSHOT|${VERSION}|g\' user-service/pom.xml"
    sh "sed -i 's|master-SNAPSHOT|${VERSION}|g\' account-service/pom.xml"
    sh "sed -i 's|master-SNAPSHOT|${VERSION}|g\' shopping-cart-service/pom.xml"
    sh "sed -i 's|master-SNAPSHOT|${VERSION}|g\' catalog-service/pom.xml"
    sh "sed -i 's|master-SNAPSHOT|${VERSION}|g\' inventory-service/pom.xml"
    sh "sed -i 's|master-SNAPSHOT|${VERSION}|g\' order-service/pom.xml"
    sh "sed -i 's|master-SNAPSHOT|${VERSION}|g\' online-store-web/pom.xml"

    sh "echo Replace master-SNAPSHOT to ${VERSION} in Dockerfile"
    sh "sed -i 's|master-SNAPSHOT|${VERSION}|g\' config-service/${sourcedockerfile}/Dockerfile"
    sh "sed -i 's|master-SNAPSHOT|${VERSION}|g\' discovery-service/${sourcedockerfile}/Dockerfile"
    sh "sed -i 's|master-SNAPSHOT|${VERSION}|g\' hystrix-dashboard/${sourcedockerfile}/Dockerfile"
    sh "sed -i 's|master-SNAPSHOT|${VERSION}|g\' edge-service/${sourcedockerfile}/Dockerfile"
    sh "sed -i 's|master-SNAPSHOT|${VERSION}|g\' user-service/${sourcedockerfile}/Dockerfile"
    sh "sed -i 's|master-SNAPSHOT|${VERSION}|g\' account-service/${sourcedockerfile}/Dockerfile"
    sh "sed -i 's|master-SNAPSHOT|${VERSION}|g\' shopping-cart-service/${sourcedockerfile}/Dockerfile"
    sh "sed -i 's|master-SNAPSHOT|${VERSION}|g\' catalog-service/${sourcedockerfile}/Dockerfile"
    sh "sed -i 's|master-SNAPSHOT|${VERSION}|g\' inventory-service/${sourcedockerfile}/Dockerfile"
    sh "sed -i 's|master-SNAPSHOT|${VERSION}|g\' order-service/${sourcedockerfile}/Dockerfile"
    sh "sed -i 's|master-SNAPSHOT|${VERSION}|g\' online-store-web/${sourcedockerfile}/Dockerfile"
}
