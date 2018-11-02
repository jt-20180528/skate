set -e

#进行打包操作
baseDir=/home/lujun/soft/jenkins/workspace
projectDir=skate
targetDir=target
env=test

proList=()
proList[0]="discovery-service"
proList[1]="config-service"
proList[2]="hystrix-dashboard"
proList[3]="inventory-service"
proList[4]="edge-service"
proList[5]="online-store-web"
proList[6]="order-service"
proList[7]="payment-service"
proList[8]="shopping-cart-service"
proList[9]="user-service"
proList[10]="account-service"
proList[11]="catalog-service"

#循环堆每个子项目打包
echo "开始打包$projectDir项目,一共${proList[@]}个子项目..."
for i in ${proList[@]}
 do
  if [ ! -d $baseDir/$projectDir/$i ]; then
     echo "项目$baseDir/$projectDir/$i不存在，请检查！"
  else 
     cd $baseDir/$projectDir/$i
	 echo "开始打包项目$i..."
	 mvn package -Dmaven.test.skip=true
	 echo "项目$i打包结束！"
  fi
 done
echo "项目$projectDir所有打包结束！"

#对每个已经打好的包进行启动
echo "开始对项目$projectDir启动..."
for i in ${proList[@]}
 do
  if [ ! -d $baseDir/$projectDir/$i/$targetDir ]; then 
    echo "项目$i不存在包，请检查！"
  else
    if [ ! -f $baseDir/$projectDir/$i/$targetDir/$i-master-SNAPSHOT.jar ]; then
	  echo "项目$i可能没有打包，请检查！"
	else
	  echo "准备启动项目$i...."
	  java -jar $baseDir/$projectDir/$i/$targetDir/$i-master-SNAPSHOT.jar --spring.profiles.active=$env
	  echo "项目$i启动成功！"
	fi
  fi
 done
 
echo "项目$projectDir启动完毕！"