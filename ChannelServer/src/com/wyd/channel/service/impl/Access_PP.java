package com.wyd.channel.service.impl;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;
import net.sf.json.JSONObject;
import com.wyd.channel.info.ChannelInfo;
import com.wyd.channel.result.LoginResult;
import com.wyd.channel.result.Result_PP;
import com.wyd.channel.service.IAccessService;
import com.wyd.channel.utils.Common;
/**
 * 
 * @author sunzx
 *
 */
public class Access_PP implements IAccessService {
    
    /** PP助手渠道编码*/
    public static final int CHANNEl_PP = 1065;

    @Override
    public JSONObject channelLogin(Map<String, Object> parameter) throws IOException {
        String tokenKey = parameter.get("tokenKey").toString();
        byte bTokenKey[] = new byte[16];
        try {
            int j = 0;
            // 将tokenKey转成byte[]
            for (int i = 0; i < tokenKey.length() - 1; i = i + 2) {
                String temp = tokenKey.substring(i, i + 2);
                byte b = (byte) Integer.parseInt(temp, 16);
                bTokenKey[j] = b;
                j++;
            }
            return this.channelLogin(bTokenKey);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException(e.getMessage());
        }
    }

    /**
     * 读取流
     * @param inStream  数据流
     * @return          转成byte后数据
     * @throws IOException
     */
    public static byte[] readStream(InputStream inStream) throws IOException {
        ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[1024];
            int len = -1;
            /* while */
            if ((len = inStream.read(buffer)) != -1) {
                outSteam.write(buffer, 0, len);
            }
            return outSteam.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException(e.getMessage());
        } finally {
            outSteam.close();
        }
    }

    /**
     * 渠道登录验证
     * @param array tokenKey数据
     * @return      验证结果
     * @throws IOException
     */
    public JSONObject channelLogin(byte[] array) throws IOException {
        byte[] b = array;
        int length = b.length + 8;
        Socket client = null;
        try {
            InetAddress inStr = InetAddress.getByName("passport_i.25pp.com");
            client = new Socket(inStr, 8080);
            client.setReuseAddress(false);
            OutputStream out = client.getOutputStream();
            ByteBuffer inbf = ByteBuffer.allocate(length);
            inbf.order(ByteOrder.LITTLE_ENDIAN);
            inbf.putInt(length);
            inbf.putInt(0xAA000022);
            inbf.put(b);
            inbf.rewind();
            out.write(inbf.array());
            out.flush();
            byte[] read = readStream(client.getInputStream());
            Map<Object, Object> map = new HashMap<Object, Object>();
            if (read.length >= Result_PP.MiniObjectSize) {
                // Result_PP result_PP = new Result_PP();
                ByteBuffer otbf = ByteBuffer.wrap(read);
                otbf.order(ByteOrder.LITTLE_ENDIAN);
                map.put("thirdReturnMessage", otbf.array());
                map.put("len", otbf.getInt());
                map.put("command", otbf.getInt());
                map.put("status", otbf.getInt());
                int status = (Integer) map.get("status");
                int len = (Integer) map.get("len");
                if (status == 0) {
                    byte busername[] = new byte[len - (3 * 4 + 8)];// 取 username 字节长度为 RecvPSData.getLen()-(3*4+8)
                    otbf.get(busername, 0, len - (3 * 4 + 8));
                    String username = new String(busername, "UTF-8");
                    map.put("userid", otbf.getInt());
                    map.put("username", username);
                    map.put("code", "0");
                } else {
                    map.put("code", "-1");
                    if (status == 0xE0000001) {
                        map.put("message", "用户名不存在");
                    } else if (status == 0xE0000002) {
                        map.put("message", "密码不正确");
                    } else if (status == 0xE00000BA) {
                        map.put("message", "账号被锁定,禁止登录");
                    } else if (status == 0xE0000101) {
                        map.put("message", "Session不存在");
                    } else if (status == 0xE0000102) {
                        map.put("message", "ResponseHash不正确");
                    } else if (status == 0xE0000006) {
                        map.put("message", "此账号无需转换");
                    } else if (status == 0xE00000DB) {
                        map.put("message", "数据库错误");
                    } else {
                        map.put("message", "未知错误");
                    }
                }
            } else {
                map.put("code", "-1");
            }
            return JSONObject.fromObject(map);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException(e.getMessage());
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 获取用户登录结果
     * @param channelInfo   渠道信息
     * @return              用户登录结果
     */
    public LoginResult getUserLoginResult(ChannelInfo channelInfo) {
        Map<String, Object> parameter = new HashMap<String, Object>();
        parameter.put("tokenKey", channelInfo.getParameter()[0]);
        LoginResult channelLoginResult = new LoginResult();
        try {
            JSONObject jsonObject = this.channelLogin(parameter);
           //PP返回的全部信息
            channelLoginResult.setThirdReturnMessage(jsonObject.get("thirdReturnMessage").toString());
            //转成jsonObject
            jsonObject.remove("thirdReturnMessage");
            channelLoginResult.setCode(Common.STATUS_SUCCESS);
            if(!"0".equals(jsonObject.get("code").toString())){
                channelLoginResult.setCode(Common.STATUS_FAIL);
            }
            channelLoginResult.setMessage(jsonObject == null ? null : jsonObject.toString());
        } catch (IOException e) {
            e.printStackTrace();
            channelLoginResult.setCode(Common.STATUS_FAIL);
            channelLoginResult.setMessage(Common.STATUS_FAIL_MESSAGE);
        }
        return channelLoginResult;
    }
}
