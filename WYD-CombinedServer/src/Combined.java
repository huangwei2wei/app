import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import com.wyd.combined.server.factory.ServiceManager;
/**
 * @author zguoqiu
 * @version 创建时间：2013-4-16 下午2:36:29 类说明
 */
public class Combined {
    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        List<String> areaList = ServiceManager.getManager().getAccountService().getAreaList();
        if (areaList.size() > 1) {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("本服有以下分区:");
            for (int i = 0; i < areaList.size(); i++) {
                System.out.println(i + 1 + "--" + areaList.get(i));
            }
            System.out.println("请从以上分区中选择两个分区进行合并:");
            int index1 = 0;
            int index2 = 0;
            while (0 == index1) {
                try {
                    System.out.println("请选择第一个分区:");
                    index1 = Integer.parseInt(br.readLine());
                    if (index1 > areaList.size()) {
                        index1 = 0;
                        throw new Exception();
                    }
                } catch (Exception ex) {
                    System.out.println("输入有误！");
                }
            }
            while (0 == index2) {
                try {
                    System.out.println("请选择第二个分区:");
                    index2 = Integer.parseInt(br.readLine());
                    if (index2 > areaList.size() || index2 == index1) {
                        index2 = 0;
                        throw new Exception();
                    }
                } catch (Exception ex) {
                    System.out.println("输入有误！");
                }
            }
            index1--;
            index2--;
            System.out.println("确认需要将分区" + areaList.get(index1) + "合并到" + areaList.get(index2) + "?(yes/no):");
            String q = null;
            while (null == q) {
                try {
                    q = br.readLine();
                    if (!"yes".equals(q) && !"no".equals(q)) {
                        q = null;
                        throw new Exception();
                    }
                } catch (Exception ex) {
                    System.out.println("输入有误！");
                }
            }
            if ("yes".equals(q)) {
                long t1 = System.currentTimeMillis();
                ServiceManager.getManager().getCombinedService().combined(areaList.get(index1), areaList.get(index2));
                System.out.println("合服完成,用时" + ((System.currentTimeMillis() - t1) / 1000) + "秒。");
                br.readLine();
            }
            br.close();
        } else {
            System.out.println("分区数量小于2，无法进行合并。");
        }
    }
}
