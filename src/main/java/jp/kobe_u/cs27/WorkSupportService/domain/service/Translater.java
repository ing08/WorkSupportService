package jp.kobe_u.cs27.WorkSupportService.domain.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 英文字を置換
 */
@Service
public class Translater {
    /**
     * 英文字を置換
     *
     * @param 文字列
     * @return 英文字以外
     */
    public static String checkExtraWord(String s) {
        Map<String, String> nameValue = new HashMap<String, String>();
        nameValue.put("tomorrow", "トゥモロー");
        nameValue.put("saga", "サガ");
        nameValue.put("Hiro", "ヒロ");

        for (Entry<String, String> e : nameValue.entrySet()) {
            s = s.replaceAll(e.getKey(), e.getValue());
        }
        return s;
    }
}
