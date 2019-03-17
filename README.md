
基础设置项目
----
为避免每次做项目从零开始特此建立此项目
提供以下基础设置
* 事务定义获取 org.qinarmy.foundation.tx.TransactionDefinitionHolder
* 读写分离配置 org.qinarmy.foundation.tx.TransactionAutoConfig
* 安全加密常用工具 org.qinarmy.foundation.security.SignatureUtils
* 邮件通知组件 .
* 模块处理,使用 HTML 模块生成用户协议等
* 业务异常层次 org.qinarmy.foundation.core.IBusinessException
* 统一的结果码 org.qinarmy.foundation.core.ResultCode
* orm 工具 org.qinarmy.foundation.orm,包含使用雪花算法生成 id
* 提供 CodeEnum 枚举方便业务使用 org.qinarmy.foundation.orm.CodeEnumUserType


