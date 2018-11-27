# cron-hms

#### 项目介绍
###  一、根据cron表达式，计算某天的那些时刻执行。以为监控做准备。
###  二、根据cron表达式，给定Date，计算下一个执行时间点。
因为是基于天分割，时、分、秒、和天、月、星期的处理不一样，但是最重要的基础就是基于cron表达式主要变化的就是时分秒，所以取名为cron-hms。

#### 软件架构
CronUtil即是项目使用入口。

#### 算法思路

###  一、根据cron表达式，计算某天的那些时刻执行
- 1、切割cron表达##式
- 2、转换每个域
- 3、计算执行时间点（关键算法，解析c##ron表达式）
- 4、计算某一天的哪些时间点执行

### 二、根据cron表达式，给定Date，计算下一个执行时间点
- 1、找到所有时分秒的组合并按照时分秒排序
- 2、给定的时分秒在以上集合之前、之后处理
- 3、给定时时分秒在以上集合中找到一个最小的位置
- 4、day+1循环直到找到满足月、星期的那一天