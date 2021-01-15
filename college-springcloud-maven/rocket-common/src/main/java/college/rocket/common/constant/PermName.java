package college.rocket.common.constant;

/**
 * @author: xuxianbei
 * Date: 2021/1/15
 * Time: 17:32
 * Version:V1.0
 */
public class PermName {
    public static final int PERM_PRIORITY = 0x1 << 3;
    public static final int PERM_READ = 0x1 << 2;
    public static final int PERM_WRITE = 0x1 << 1;
    public static final int PERM_INHERIT = 0x1 << 0;

    public static boolean isReadable(final int perm) {
        return (perm & PERM_READ) == PERM_READ;
    }

    public static boolean isWriteable(final int perm) {
        return (perm & PERM_WRITE) == PERM_WRITE;
    }

    public static boolean isInherited(final int perm) {
        return (perm & PERM_INHERIT) == PERM_INHERIT;
    }

    public static void main(String[] args) {
        System.out.println(PERM_READ);
        System.out.println(PERM_WRITE);
    }
}
