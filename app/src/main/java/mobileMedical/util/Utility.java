package mobileMedical.util;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.Calendar;
import java.util.Date;

	public class Utility {
	    public static void setListViewHeightBasedOnChildren(ListView listView) {
	            //获取ListView对应的Adapter
	        ListAdapter listAdapter = listView.getAdapter(); 
	        if (listAdapter == null) {
	            // pre-condition
	            return;
	        }

	        int totalHeight = 0;
	        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {   //listAdapter.getCount()返回数据项的数目
	            View listItem = listAdapter.getView(i, null, listView);
	            listItem.measure(0, 0);  //计算子项View 的宽高
	            totalHeight += listItem.getMeasuredHeight();  //统计所有子项的总高度
	        }

	        ViewGroup.LayoutParams params = listView.getLayoutParams();
	        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
	        //listView.getDividerHeight()获取子项间分隔符占用的高度
	        //params.height最后得到整个ListView完整显示需要的高度
	        listView.setLayoutParams(params);
	    }

	    public static String getAgeByBirthday(String birth) {
	    	if(birth.equalsIgnoreCase("")){
	    		return "";
	    	}
			 Date birthday=new Date(birth.toString().trim().replace('-','/') );
			Calendar cal = Calendar.getInstance();

			if (cal.before(birthday)) {
				throw new IllegalArgumentException(
						"The birthDay is before Now.It's unbelievable!");
			}

			int yearNow = cal.get(Calendar.YEAR);
			int monthNow = cal.get(Calendar.MONTH) + 1;
			int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);

			cal.setTime(birthday);
			int yearBirth = cal.get(Calendar.YEAR);
			int monthBirth = cal.get(Calendar.MONTH) + 1;
			int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);

			int age = yearNow - yearBirth;

			if (monthNow <= monthBirth) {
				if (monthNow == monthBirth) {
					// monthNow==monthBirth 
					if (dayOfMonthNow < dayOfMonthBirth) {
						age--;
					}
				} else {
					// monthNow>monthBirth 
					age--;
				}
			}
			return age+"";
		}
	    
	    /**
	     * 得到中文首字母缩写
	     * 
	     * @param str
	     * @return
	     */
	    public static String getPinYinHeadChar(String str) {

	        String convert = "";
	        for (int j = 0; j < str.length(); j++) {
	            char word = str.charAt(j);
	            String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);
	            if (pinyinArray != null) {
	                convert += pinyinArray[0].charAt(0);
	            } else {
	                convert += word;
	            }
	        }
	        return convert.toUpperCase();
	    }
	    
	    /**
		 * 得到 全拼
		 * 
		 * @param src
		 * @return
		 */
		public static String getPingYin(String src) {
			char[] t1 = null;
			t1 = src.toCharArray();
			String[] t2 = new String[t1.length];
			HanyuPinyinOutputFormat t3 = new HanyuPinyinOutputFormat();
			t3.setCaseType(HanyuPinyinCaseType.LOWERCASE);
			t3.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
			t3.setVCharType(HanyuPinyinVCharType.WITH_V);
			String t4 = "";
			int t0 = t1.length;
			try {
				for (int i = 0; i < t0; i++) {
					// 判断是否为汉字字符
					if (java.lang.Character.toString(t1[i]).matches(
							"[\\u4E00-\\u9FA5]+")) {
						t2 = PinyinHelper.toHanyuPinyinStringArray(t1[i], t3);
						t4 += t2[0];
					} else {
						t4 += java.lang.Character.toString(t1[i]);
					}
				}
				return t4;
			} catch (BadHanyuPinyinOutputFormatCombination e1) {
				e1.printStackTrace();
			}
			return t4;
		}
}
