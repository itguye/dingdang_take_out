### 1.项目概述
项目分为后台管理端,前台移动端和垃圾清理端:

#### (1) 后台管理端
![在这里插入图片描述](https://img-blog.csdnimg.cn/b3ef60c37c37441a9f4ca05f8b39c00f.png)

#### (2) 前台移动端
![在这里插入图片描述](https://img-blog.csdnimg.cn/dd0afc300b974da993bd05a793401e94.png)
#### (3) 垃圾清理端
> 垃圾清理源码:[https://github.com/itguye/dingdangtakeout_quartz_clearpictures.git](https://github.com/itguye/dingdangtakeout_quartz_clearpictures.git)


清理Redis中的缓存和七牛云的垃圾数据:
![在这里插入图片描述](https://img-blog.csdnimg.cn/1dfbda3ddc744724ba87ce01b0fbf2de.png)

清理数据库和七牛云中的垃圾数据:
![在这里插入图片描述](https://img-blog.csdnimg.cn/3a18b1d8404141a9bfd36f85af8f9658.png)



 ### 2.技术栈
> 后台管理端和前台移动端主要使用SpringBoot+Mybatis-Plus实现数据库的CRUD操作,项目中的图片上传与下载采用七牛云,数据缓存使用Redis,共二种方式 Spring Data Redis和SpringCache,垃圾清理端主要采用Spring+MyBatis实现数据的查询与删除操作,采用Quartz定时组件实现每周星期天晚上23点清理数据库中的垃圾数据(被后台管理端删除后的数据,采用了逻辑删除),每日晚23点清理Redis缓存数据(用于记录七牛云中所有上传图片和上传到数据库中图片的数据)和七牛云中的垃圾数据。
> 
> 相关知识点如下: 
> ① SpringBoot和Spring
> ② MyBatis 和 Mybatis-Plus
> ③ Redis
> ④ Spring Data Redis和SpringCache
> ⑤ Mysql
> ⑥ Quartz定时组件
> ⑦ 七牛云


![在这里插入图片描述](https://img-blog.csdnimg.cn/9413e0de4038477bafddc3dbd9608875.png)

 ### 3.服务器环境搭建(Linux环境)
 
> 安装可参考下面三篇博客:
> [Linux centos7.0搭建Java开发环境(保姆级教程)](https://blog.csdn.net/weixin_42753193/article/details/125964013),
> [Linux centOs7.0安装宝塔面板(保姆级教程)](https://blog.csdn.net/weixin_42753193/article/details/125959289)和
> [Linux安装maven(详细教程)](https://blog.csdn.net/weixin_58276266/article/details/122566931?ops_request_misc=%257B%2522request%255Fid%2522%253A%2522165920414716781432938137%2522%252C%2522scm%2522%253A%252220140713.130102334..%2522%257D&request_id=165920414716781432938137&biz_id=0&utm_medium=distribute.pc_search_result.none-task-blog-2~all~sobaiduend~default-1-122566931-null-null.142%5Ev35%5Epc_search_result_control_group&utm_term=linux%20%E5%AE%89%E8%A3%85Maven&spm=1018.2226.3001.4187)
> 服务器端需要安装如下软件:
>① JDK
>② Tomcat
>③ 宝塔面板
>④ Redis
>⑤ Mysql
>⑥ Maven
> 

备注:Redis和MySql我是通过宝塔面板进行安装。
![在这里插入图片描述](https://img-blog.csdnimg.cn/bbb6a724d3dd4b1f9e7af28262b1a2c7.png)


④ 七牛云
> 访问七牛云官网[https://www.qiniu.com/](https://www.qiniu.com/),你需要注册用户,并且实名认证,并申请空间,然后将图片上传到空间即可

![在这里插入图片描述](https://img-blog.csdnimg.cn/0f111ed7ff1248aebb2a83fc96bf433e.png)
所需上传图片放在项目中的图片资源里
![在这里插入图片描述](https://img-blog.csdnimg.cn/d743205f3a924e4aa03c6326b6a18fa8.png)


### 4.软件部署
#### (1) 数据库部署

> 这里我采用的是宝塔面板部署数据库,数据库的SQL文件放在项目中的db文件下。

添加数据库的账号和密码就是你的远程服务器的数据库账号密码(意思就是在服务器中新建了一个mysql用户)
![在这里插入图片描述](https://img-blog.csdnimg.cn/1c3ef4e4bbf04bba80acd9c147d29674.png)
导入数据库的SQL
![在这里插入图片描述](https://img-blog.csdnimg.cn/85b9ebda25814d45a688d5c219f58fa2.png)
![在这里插入图片描述](https://img-blog.csdnimg.cn/07ee0c84fd054292b653c9cc613bd348.png)
sql文件存放在项目的db文件下:
![在这里插入图片描述](https://img-blog.csdnimg.cn/649900e3eec341aea8d1df89790d1aad.png)

用户授权(用户授权需要安装phpMyAdmin软件)
![在这里插入图片描述](https://img-blog.csdnimg.cn/928b7369a06042f19cf267940382002e.png)
登入的用户名和密码就是你的root账户
![在这里插入图片描述](https://img-blog.csdnimg.cn/137dd781ca1e47f1b7377dd0db156a83.png)
创建的数据库需要远程连接权限和新建用户对新建数据库的增删改查权限
![在这里插入图片描述](https://img-blog.csdnimg.cn/922a35c7f01441828ff7f3fc65ed1080.png)
新增用户权限,这里的用户就是自己新增的用户(业务程序访问数据库的用户)
![在这里插入图片描述](https://img-blog.csdnimg.cn/432a2d5c051a456086b1561fab746883.png)


![在这里插入图片描述](https://img-blog.csdnimg.cn/2a6b9d67d07b4fa882652312044cab10.png)


#### (2) 相关参数配置

> 这里主要讲解对前台和后台部分的配置,垃圾清理部分的配置只有mysql和Mybatis,只需要修改成自己的配置参数即可，不在演示。

 - 项目clone 或下载源码

```git
git clone https://github.com/itguye/dingdang_take_out.git
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/498af01c24074425a498764d83256911.png)


- IDEA打开项目(Maven导包需要一些时间)

![在这里插入图片描述](https://img-blog.csdnimg.cn/10db499590884c4ab04865516f650ed9.png)

- 修改项目配置文件application.yaml

服务端口号与数据库配置:

![在这里插入图片描述](https://img-blog.csdnimg.cn/e048c23decc047cf83117b806974f2af.png)

Redis和mybatis-plus的配置:

![在这里插入图片描述](https://img-blog.csdnimg.cn/9022e88f7a1c415fbd7d5eee513281da.png)






- 文件上传与下载七牛云配置

修改七牛云工具类的配置:

![在这里插入图片描述](https://img-blog.csdnimg.cn/81e597cb865b4da7b3a45ff14bbffcb5.png)

修改如下参数:

![在这里插入图片描述](https://img-blog.csdnimg.cn/0da27e42e9054c65a2422ef10228b93f.png)


> accessKey和secretKey对于七牛云个人中心密钥管理中心的AK和SK:

![在这里插入图片描述](https://img-blog.csdnimg.cn/ddafd630fccf42bbbc67c5302738d6b0.png)

> bucket对于七牛云中的空间名

![在这里插入图片描述](https://img-blog.csdnimg.cn/25147a7395eb432292da4337552d2e56.png)

>  **`new Configuration(Zone.zone2());`** 表示存储在华南区域,其他区域分别为Zone.zone0华东 ,Zone.zone1华北,Zone.zone2华南,根据你选择的区域进行配置即可

![在这里插入图片描述](https://img-blog.csdnimg.cn/8aff0d4e360e4631bf9b166e7409ef63.png)

![在这里插入图片描述](https://img-blog.csdnimg.cn/4ff6f14946ca46608676b4d11ac760a4.png)

 - 图片的游览是直接访问七牛云(外链),需要修改成你的外链主机名

> 在七牛云中复制外链接如 http://rfuczc8jn.hn-bkt.clouddn.com/00874a5e-0df2-446b-8f69-a30eb7d88ee8.png,我的 **`外链接主机名为rfuczc8jn.hn-bkt.clouddn.com`** ,在前端需要修改成你的外链接主机名,不然无法获取你七牛云里的图片,外链接就是直接通过互联网就可以进行访问。

![在这里插入图片描述](https://img-blog.csdnimg.cn/042bbc62eee44b6595f128f997e5fdff.png)

你可以通过全局搜索进行快速定位

![在这里插入图片描述](https://img-blog.csdnimg.cn/71f239c09b354a42a75423c29b839322.png)
前端如下几处需要进行修改:

后台管理端:

![在这里插入图片描述](https://img-blog.csdnimg.cn/faa23574c4cc432db12aae0bf8621805.png)
![在这里插入图片描述](https://img-blog.csdnimg.cn/00976a03710540549930ab98a3532c66.png)
![在这里插入图片描述](https://img-blog.csdnimg.cn/a7c2618716d94de3bbe6b6a56a9bc1bf.png)

![在这里插入图片描述](https://img-blog.csdnimg.cn/5dc982a8f97347189aa1fceb38e044ce.png)
![在这里插入图片描述](https://img-blog.csdnimg.cn/a023b95d5d8f4372b054ab26bfe386e0.png)

![在这里插入图片描述](https://img-blog.csdnimg.cn/c907c5be23414a4690847e0cb1b96752.png)

前台移动端:
![在这里插入图片描述](https://img-blog.csdnimg.cn/28f659c8e6e345899232a767506dad28.png)
![在这里插入图片描述](https://img-blog.csdnimg.cn/909a798e54b54692bca040d0bcdffe54.png)


备注:垃圾清理部分的配置就是修改一下Redis和mysql的配置即可,这里不再演示了。
#### (3) 程序部署

> 项目的前台和后台是同一个项目,垃圾清理是另一个项目,前一个项目打包方式为Jar方式,运行非常简单通过 **`java -jar 项目名`** 运行即可,当然这部分和视频中的一致可以通过Shell脚本的方式进行部署，后一个项目打包方式为war包,将war放入到Tomcat的webapps中即可运行,二个项目都是通过Maven构建,所以生成jar/war只需要通过mvn package进行打包,然后将打包后的文件上传到服务器中即可,如果是前一个项目通过shell脚本执行的话,将修改后的代码push到托管代码平台上(gittee/github)上,服务器端先clone项目,如果下次有更新直接执行脚本即可更新项目。

shell存放在项目中的shell文件下:
![在这里插入图片描述](https://img-blog.csdnimg.cn/95a27039c755428e83b7ae6521929911.png)

- 第一个项目的部署(后台服务端与前台服务端叮当外卖)

通过脚本的方式执行程序(需要先在Linux相应文件下git clone自己的项目,更新后执行下面的脚本即可)

![在这里插入图片描述](https://img-blog.csdnimg.cn/b4c4e60aba9f45a1bafa596556b48ab2.png)

当然你也可以通过执行 **`java -jar 项目`** 的方式执行项目

![在这里插入图片描述](https://img-blog.csdnimg.cn/706f8bb5a93e4c3b921d6975509db6ec.png)

 - 第二个项目的部署,垃圾清理

只需要将生成后的war包放入到Tomcat下的webapps中即可

![在这里插入图片描述](https://img-blog.csdnimg.cn/6553c1c4635144079b5cb82457cef5e6.png)


### 5.软件预览
#### (1) 后台管理端

> 后台管理端访问路径为:[http://124.220.28.236:8089/backend/page/login/login.html](http://124.220.28.236:8089/backend/page/login/login.html)(账号zhangsan,密码:123456)
 

 - 后台登入


![在这里插入图片描述](https://img-blog.csdnimg.cn/ddc6805525f74da182c776b411ac8748.png)

 - 员工管理

![在这里插入图片描述](https://img-blog.csdnimg.cn/8a643ebdad6d4a729e94576e7b6ebf1c.png)

 - 分类管理

![在这里插入图片描述](https://img-blog.csdnimg.cn/443742e14c45427e9adacecbe69deadd.png)

- 菜品管理(批量处理业务视频中未编写,这里我编写了的)

![在这里插入图片描述](https://img-blog.csdnimg.cn/bcfa2db9f9794fb8b7c162dd2ed5f8f7.png)

- 套餐管理(批量处理业务视频中未编写,这里我编写了)

![在这里插入图片描述](https://img-blog.csdnimg.cn/886dea1f6b2a4a02aeb67c3cc4cad5c7.png)

- 订单详情(视频中该本分业务未编写,这里我编写了)

![在这里插入图片描述](https://img-blog.csdnimg.cn/88aafab8bfa54da396c05070b6be9a84.png)
#### (2) 前台移动端

> 游览器访问需要手机适配设置(按F12适配手机),访问路径: [http://124.220.28.236:8089/front/page/login.html](http://124.220.28.236:8089/front/page/login.html)（手机号:13812345678,点击获取验证码,进行登入即可）
> 
![在这里插入图片描述](https://img-blog.csdnimg.cn/f3bd136ab1a14f8b9f273f8bafbd6ea1.png)




- 登入页面

![在这里插入图片描述](https://img-blog.csdnimg.cn/72375f2a998e4c3f8750e60a66211f4d.png)

- 服务大厅

![在这里插入图片描述](https://img-blog.csdnimg.cn/2cfc7458eb02458fa23a2b2a23d978ed.png)

- 订单结算页面

![在这里插入图片描述](https://img-blog.csdnimg.cn/3cda66d0cf53483da4017074138b43a4.png)

- 个人中心

![在这里插入图片描述](https://img-blog.csdnimg.cn/ac3fd526ef3a4c4ebbc5b9b7ff7d5d29.png)

- 地址管理

![在这里插入图片描述](https://img-blog.csdnimg.cn/83f3c621063c4c6abea3b4b30dfc7968.png)

- 历史订单

![在这里插入图片描述](https://img-blog.csdnimg.cn/f08b317dd8444e7d818422ba323097c3.png)
#### (3) 垃圾清理端(war包)

> 垃圾清理打包成war放入Tomcat的webapps中,由于定时组件的原因会根据core表达式在某一个具体时间执行,共二个任务,任务1执行清理Redis缓存和七牛云的垃圾数据,任务2执行清理数据库中和七牛云的垃圾数据。

清理Redis中的缓存和七牛云的垃圾数据:

![在这里插入图片描述](https://img-blog.csdnimg.cn/1dfbda3ddc744724ba87ce01b0fbf2de.png)

清理数据库和七牛云中的垃圾数据:

![在这里插入图片描述](https://img-blog.csdnimg.cn/3a18b1d8404141a9bfd36f85af8f9658.png)
