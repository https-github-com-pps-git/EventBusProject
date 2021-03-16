TODO  EventBus  原理  其实就是 通过注解 获取注解处理器然后生成代码  最后在反射调用这个类中的方法

 /**
 * 反射 MainActivity 调用 MainActivity 中的 fun1方法
 */

    Class mainClazz = MainActivity.class;
    Method fun1Method = mainClazz.getDeclaredMethod("fun1", String.class);
    fun1Method.setAccessible(true);
    fun1Method.invoke(activity,"哈哈哈哈");

    @EventBus 有限制只能是一个参数

    应为我们要通过注解处理器生成一个map集合 保存的有 当前的对象   (注解的信息)方法名称  参数的类型 EventBusBean
    Map<object,List<EventBusBean>>

    EventBusBean(方法名字,参数类型)

    然后呢 在每次注册的时候我们又去定义一个map集合 把当前存在的对象保存起来,在对象销毁之后呢 把它移除
    就是说 注册的时候 把当前的对象作为key  然后去生产的代码中查找到这个对象包含的EventBusBean

    然后在send消息的时候我们就去遍历这个集合 找到对象的方法 然后反射执行

    在remove的时候从这个map中移除这个key




