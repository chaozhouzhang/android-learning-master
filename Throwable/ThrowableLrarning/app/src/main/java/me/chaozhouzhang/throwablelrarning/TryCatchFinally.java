package me.chaozhouzhang.throwablelrarning;

/**
 * Created on 2019/3/8 11:02
 *
 * @author zhangchaozhou
 */
public class TryCatchFinally {
    /**
     * 1、不管有没有异常，finally中的代码都会执行。
     * <p>
     * 2、当try、catch中有return或throw时，finally中的代码依然会继续执行。
     * <p>
     * 3、finally是在try和catch的return或throw前执行的，但是此时并没有直接返回值，而是把值保存起来，不管finally对该值做任何的改变，返回的值都不会改变，依然返回保存起来的值。
     * 也就是说方法的返回值是在finally运算之前就确定了的。
     * <p>
     * 4、finally代码中最好不要包含return或throw，程序会提前退出，也就是说返回的值不是try或catch中的值。
     * <p>
     * 最总结论：在执行try、catch中的return或throw之前一定会执行finally中的代码（如果finally存在），如果finally中有return或throw语句，就会直接执行finally中的return或throw方法，
     * 所以finally中的return或throw语句一定会被执行的。
     * <p>
     * 5、出现Exception意外的异常致使方法非正常退出的时候，方法没有返回值。
     */
    public static void main(String[] args) {
        System.out.println("无异常，try有return：" + tryCatchFinallyTryReturn());
        System.out.println("\n");
        System.out.println("有异常，catch有return：" + tryCatchFinallyCatchReturn());
        System.out.println("\n");
        System.out.println("finally有return：" + tryCatchFinallyFinallyReturn());
    }


    private static int tryCatchFinallyTryReturn() {
        int result;
        try {
            result = 1;
            System.out.println("try包裹的内容 try有return");
            return result;
        } catch (Exception e) {
            result = 0;
            System.out.println("try包裹的内容有异常 catch有return");
            return result;
        } finally {
            result = -1;
            System.out.println("执行finally语句，finally没有return，此处不会返回，并且不会修改返回的值：" + result);
        }
    }

    private static int tryCatchFinallyCatchReturn() {
        int result;
        try {
            result = 1;
            System.out.println("try包裹的内容 try有return");
            int compute = 8 / 0;
            System.out.println("计算结果：" + compute);
            return result;
        } catch (Exception e) {
            result = 0;
            System.out.println("try包裹的内容有异常 catch有return");
            return result;
        } finally {
            System.out.println("执行finally语句");
        }
    }

    private static int tryCatchFinallyFinallyReturn() {
        int result;
        try {
            result = 1;
            System.out.println("try包裹的内容 try有return");
            int compute = 8 / 0;
            System.out.println("计算结果：" + compute);
            return result;
        } catch (Exception e) {
            result = 0;
            System.out.println("try包裹的内容有异常 catch有return");
            return result;
        } finally {
            result = -1;
            System.out.println("执行finally语句，finally有return，此处会直接返回finally修改的值：" + result);
            return result;
        }
    }

}
