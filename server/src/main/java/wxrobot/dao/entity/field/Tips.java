package wxrobot.dao.entity.field;

import lombok.Data;

/**
 * @ClassName: Tips
 * @Description: 提示语实体类
 * @author Crap
 * @date 2019年3月5日
 * 
 */

@Data
public class Tips {

	/**
	 * @Fields 发现新群提示语
	 */
	private Msg chatRoomFoundTip;

	/**
	 * @Fields 成员加入提示语
	 */

	private Msg memberJoinTip;

	/**
	 * @Fields 成员退出提示语
	 */

	private Msg memberLeftTip;
}
