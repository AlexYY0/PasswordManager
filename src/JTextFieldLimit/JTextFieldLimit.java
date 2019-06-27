package JTextFieldLimit;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JTextFieldLimit extends PlainDocument {

    private int limit;  //限制的长度

    public JTextFieldLimit(int limit) {
        super(); //调用父类构造
        this.limit = limit;
    }
    public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
        Pattern num = Pattern.compile("[0-9]*");
        Matcher n = num.matcher(str);
        Pattern letter=Pattern.compile("[a-zA-Z]");
        Matcher l = letter.matcher(str);
        if(str == null) return;

        if((n.matches()||l.matches())&&(getLength() + str.length()) <= limit){
            super.insertString(offset, str, attr);//调用父类方法
        }else {
            JOptionPane.showMessageDialog(null, "最多输入11位，且只能输入字母,数字！", "注意", JOptionPane.INFORMATION_MESSAGE);
        }
    }

}
