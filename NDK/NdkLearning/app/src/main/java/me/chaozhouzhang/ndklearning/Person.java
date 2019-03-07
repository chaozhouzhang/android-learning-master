package me.chaozhouzhang.ndklearning;

/**
 * Created on 2019/3/7 20:29
 *
 * @author zhangchaozhou
 */
public class Person {

    private int age;
    private String threadName;

    public int getAge() {
        return age;
    }

    public Person setAge(int age) {
        this.age = age;
        return this;
    }

    public String getThreadName() {
        return threadName;
    }

    public Person setThreadName(String threadName) {
        this.threadName = threadName;
        return this;
    }

    private void setValue(int value) {
        age = value;
        threadName = Thread.currentThread().getName() + " is In Method of setValue";
    }
}
