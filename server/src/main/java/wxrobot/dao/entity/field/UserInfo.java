package wxrobot.dao.entity.field;

import java.util.Date;

import lombok.Data;

/**
 * 用户基本信息
 * 
 * @author wr
 */
@Data
public class UserInfo {

	private String userName;
	private String userPwd;
	/** 昵称 */
	private String nickName;
	private Date regTime = new Date();
	/** 软件到期时间 */
	private Date serverEnd=new Date();
	/** 软件服务状态 */
	private Boolean serverState;
	private String phoneNum;
	/** 手机认证状态 */
	private Boolean phoneState=false;
	/** 帐号来源0管理员添加，1注册*/
	private int comefrom=0;
	private String regIp;
}
