package jp.kobe_u.cs27.WorkSupportService.domain.service;

import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Map.Entry;

import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import static jp.kobe_u.cs27.WorkSupportService.configration.exception.ErrorCode.*;
import jp.kobe_u.cs27.WorkSupportService.configration.exception.OtherValidationException;
import jp.kobe_u.cs27.WorkSupportService.configration.exception.WorkLogValidationException;
import jp.kobe_u.cs27.WorkSupportService.configration.exception.WorkStateValidationException;
import jp.kobe_u.cs27.WorkSupportService.domain.dto.WorkLogWithWnameDto;
import jp.kobe_u.cs27.WorkSupportService.domain.dto.WorkTimeWithWnameDto;
import jp.kobe_u.cs27.WorkSupportService.domain.entity.Work;
import jp.kobe_u.cs27.WorkSupportService.domain.entity.WorkEvent;
import jp.kobe_u.cs27.WorkSupportService.domain.entity.WorkLog;
import jp.kobe_u.cs27.WorkSupportService.domain.entity.WorkState;
import jp.kobe_u.cs27.WorkSupportService.domain.repositry.UserRepositry;
import jp.kobe_u.cs27.WorkSupportService.domain.repositry.WorkLogRepositry;
import jp.kobe_u.cs27.WorkSupportService.domain.repositry.WorkStateRepositry;

/**
 * 作業ログのサービス
 *
 * @author ing
 */
@Service
@RequiredArgsConstructor
public class WorkLogService {
    /** 作業ログのリポジトリ */
    private final WorkLogRepositry wlr;
    /** 作業状況のリポジトリ */
    private final WorkStateRepositry wsr;
    /** ユーザのリポジトリ */
    private final UserRepositry ur;

    /** 作業イベントのサービス */
    private final WorkEventService wes;
    /** 作業のサービス */
    private final WorkService ws;
    /** ログインログのサービス */
    private final LoginLogService lls;
    /** 英文字置換 */
    private final Translater t;

    /* -------------------- Create -------------------- */

    // /**
    // * 作業ログを作成
    // *
    // * @param startwe
    // * @param endwe
    // * @return WorkLog
    // */
    // public WorkLog createWorkLog(WorkEvent startwe, WorkEvent endwe) {
    // int second = (int) (endwe.getCreatedAt().getTime() -
    // startwe.getCreatedAt().getTime()) / 1000; // 作業時間sを計算

    // return wlr.save(new WorkLog(null, endwe.getUid(), endwe.getWid(),
    // startwe.getCreatedAt(), endwe.getCreatedAt(),
    // second, startwe.getEid(), endwe.getEid())); // 作業ログを作成
    // }

    /**
     * 作業ログを手動で作成
     *
     * @param uid
     * @param wid
     * @param datesincest
     * @param dateuntilst
     * @return WorkLog
     */
    public WorkLog createWorkLogManually(String uid, String wid, String datesincest, String timesincest,
            String dateuntilst, String timeuntilst) {
        WorkState state = wsr.findById(uid).orElseThrow(() -> new WorkStateValidationException(WORKSTATE_DOES_NOT_EXIST,
                "get the workState", String.format("user %s workState dose not exist", uid))); // ユーザの作業状況を所得

        if (!state.getWid().equals("0")) { // ユーザが作業中の場合
            throw new WorkLogValidationException(WORKLOG_CAN_NOT_CREATE_WORKING, "create the workLog",
                    String.format("workLog can not create %s working", uid)); // 例外を投げる
        }

        Date since = new Date();
        Date until = new Date();

        try {
            SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            // String型をDate型に変換する
            since = sdFormat.parse(datesincest + " " + timesincest);
            until = sdFormat.parse(dateuntilst + " " + timeuntilst);
        } catch (ParseException e) {
            throw new OtherValidationException(PERSE_EXCEPTION, "create the WorkLog",
                    String.format("ParseException occured")); // 例外を投げる
        }

        if (since.equals(until)) { // sinceとuntilが同じ時刻だったら
            throw new WorkLogValidationException(UNTIL_AND_SINCE_CAN_NOT_SAME, "create the Worklog",
                    String.format("until %s and since %s can not set same", until, since)); // 例外を投げる
        }

        Date now = new Date(); // 現在の時刻を所得

        if (since.after(now) || until.after(now)) { // 過去への記録でなければ
            throw new WorkLogValidationException(EVENT_NOT_FAST, "create the WorkEvent",
                    String.format("workEvent can not record in the future")); // 例外を投げる
        }

        if (since.after(until)) { // 作業開始イベントが作業終了イベントよりも未来に指定されていたら
            throw new WorkLogValidationException(STARTEVENT_NOT_OLDER_ENDEVENT, "create the WorkEvent",
                    String.format("startWorkEvent can not record older than endWorkEvent")); // 例外を投げる
        }

        WorkEvent before = wes.getNewestWorkEventSince(uid, since); // startについて直前の作業イベントを所得

        if (((before != null) && before.getType().equals("start"))) { // startの直前の作業イベントのタイプがstartだったら
            throw new WorkLogValidationException(EVENT_START_END_NOT_MATCH, "create the startWorkEvent", String
                    .format("startWorkEvent can not record following startWorkEvent %s just before", before.getEid())); // 例外を投げる
        }

        WorkEvent after = wes.getOldestWorkEventUntil(uid, until); // endについて直後の作業イベントを所得

        if (((after != null) && after.getType().equals("end"))) { // endの直後の作業イベントのタイプがendだったら
            throw new WorkLogValidationException(EVENT_START_END_NOT_MATCH, "create the endWorkEvent",
                    String.format("endWorkEvent can not record following endWorkEvent %s just before", after.getEid())); // 例外を投げる
        }

        WorkEvent startwe = wes.startWorkEventManually(uid, wid, since); // 作業開始イベントを作成
        WorkEvent endwe = wes.endWorkEventManually(uid, wid, until); // 作業終了イベントを作成

        int second = (int) (endwe.getCreatedAt().getTime() - startwe.getCreatedAt().getTime()) / 1000; // 勉強時間sを計算

        Date lastlogin = new Date();

        lls.updateLoginLog(uid, lastlogin); // ログインログを更新

        return wlr.save(
                new WorkLog(null, uid, wid, since, until, second, startwe.getEid(), endwe.getEid(), lastlogin, false)); // WorkLogを記録
    }

    /* -------------------- Read -------------------- */

    /**
     * ユーザに紐づくすべての作業ログを作業名付きで所得
     *
     * @param uid
     * @return List<WorkLogWithWnameDto>
     */
    public List<WorkLogWithWnameDto> getAllWorkLogByUid(String uid) {
        ArrayList<WorkLogWithWnameDto> list = new ArrayList<WorkLogWithWnameDto>();

        String wname;

        for (WorkLog log : wlr.findByUidOrderBySinceDesc(uid)) {
            String wid = log.getWid();

            if (!ws.existsWork(wid)) { // 作業が存在しないなら
                wname = "削除済み";
            } else {
                wname = ws.getWork(wid).getWname(); // 作業名を所得
            }

            WorkLogWithWnameDto dto = new WorkLogWithWnameDto();

            list.add(dto.build(log, wname)); // リストに作業名付き作業ログを追加
        }

        return list;
    }

    // /**
    // * ユーザに紐づく直前の作業ログを作業名付きで所得
    // *
    // * @param uid
    // * @return WorkLogWithWnameDto
    // */
    // public WorkLogWithWnameDto getNewestWorkLogByUid(String uid) {
    // WorkLog log = wlr.findFirstByUidOrderBySinceDesc(uid); // 直前の作業ログを所得

    // String wname = ws.getWork(log.getWid()).getWname(); // 作業名を所得

    // WorkLogWithWnameDto dto = new WorkLogWithWnameDto();

    // return dto.build(log, wname); // 作業名付きの作業ログ
    // }

    /**
     * ユーザの作業ログを期間を指定して所得
     *
     * @param uid
     * @param sincest
     * @param untilst
     * @return List<WorkLogWithWnameDto>
     */
    public List<WorkLogWithWnameDto> getWorkLogSettingPeriod(String uid, String sincest, String untilst) {
        ArrayList<WorkLogWithWnameDto> list = new ArrayList<WorkLogWithWnameDto>();

        Date since = new Date();
        Date until = new Date();

        try {
            SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd");

            // String型をDate型に変換する
            since = sdFormat.parse(sincest);
            until = sdFormat.parse(untilst);
        } catch (ParseException e) {
            throw new OtherValidationException(PERSE_EXCEPTION, "create the WorkLog",
                    String.format("ParseException occured")); // 例外を投げる
        }

        Calendar cal = Calendar.getInstance();

        // 1日加算
        cal.setTime(until);
        cal.add(Calendar.DATE, 1);
        until = cal.getTime();

        String wname;

        for (WorkLog log : wlr.findByUidAndUntilBetweenOrderBySinceDesc(uid, since, until)) {
            String wid = log.getWid();

            if (!ws.existsWork(wid)) { // 作業が存在しないなら
                wname = "削除済み";
            } else {
                wname = ws.getWork(wid).getWname(); // 作業名を所得
            }

            WorkLogWithWnameDto dto = new WorkLogWithWnameDto();

            list.add(dto.build(log, wname)); // リストに作業名付き作業ログを追加
        }

        return list;
    }

    /**
     * 作業時間を計算
     *
     * @param List<WorkLogWithWnameDto>
     * @return List<WorkTimeWithWnameDto>
     */
    public List<WorkTimeWithWnameDto> caluculateAllWorkTime(List<WorkLogWithWnameDto> logs) {
        ArrayList<WorkTimeWithWnameDto> list = new ArrayList<WorkTimeWithWnameDto>();

        int all = 0;
        int plus;
        int other = 0;

        for (Work work : ws.getAllWork()) {
            int second = 0;

            for (WorkLogWithWnameDto log : logs) {
                if (log.getWid().equals(work.getWid())) { // 作業ごとに
                    plus = log.getSecond();
                    second += plus; // 作業時間を計算
                    all += plus; // 合計の作業時間を計算
                }
            }
            if (second > 0) {
                WorkTimeWithWnameDto dto = new WorkTimeWithWnameDto();

                list.add(dto.build(work, second));
            }
        }

        // 削除済みの作業の作業時間を集計
        for (WorkLogWithWnameDto log : logs) {
            if (!ws.existsWork(log.getWid())) {
                plus = log.getSecond();
                other += plus;
                all += plus;
            }
        }

        if (other > 0) {
            WorkTimeWithWnameDto dto = new WorkTimeWithWnameDto();

            list.add(dto.build(new Work("unspecified", "削除済み"), other));
        }

        // 作業時間を降順に整列
        Collections.sort(list, new Comparator<WorkTimeWithWnameDto>() {
            @Override
            public int compare(WorkTimeWithWnameDto timefirst, WorkTimeWithWnameDto timesecond) {
                return Integer.compare(timesecond.getSecond(), timefirst.getSecond());
            }
        });

        WorkTimeWithWnameDto dto = new WorkTimeWithWnameDto();

        list.add(dto.build(new Work("all", "計"), all));

        return list;
    }

    /**
     * ユーザと作業に紐づく作業ログを作業名付きで所得
     *
     * @param uid
     * @param wid
     * @return List<WorkLogWithWnameDto>
     */
    public List<WorkLogWithWnameDto> getWorkLogByWid(String uid, String wid) {
        ArrayList<WorkLogWithWnameDto> list = new ArrayList<WorkLogWithWnameDto>();

        String wname;

        if (wid.equals("unspecified")) { // 作業が未指定だった場合
            for (WorkLog log : wlr.findByUidOrderBySinceDesc(uid)) {
                // String wid = log.getWid();

                if (!ws.existsWork(log.getWid())) { // 作業が存在しないなら
                    wname = "削除済み";

                    WorkLogWithWnameDto dto = new WorkLogWithWnameDto();

                    list.add(dto.build(log, wname)); // リストに作業名付き作業ログを追加
                }
            }
        } else {
            for (WorkLog log : wlr.findByUidAndWidOrderBySinceDesc(uid, wid)) {
                // String wid = log.getWid();

                if (!ws.existsWork(wid)) { // 作業が存在しないなら
                    wname = "削除済み";
                } else {
                    wname = ws.getWork(wid).getWname(); // 作業名を所得
                }

                WorkLogWithWnameDto dto = new WorkLogWithWnameDto();

                list.add(dto.build(log, wname)); // リストに作業名付き作業ログを追加
            }
        }

        return list;
    }

    /**
     * ユーザの作業ログを作業と期間を指定して所得
     *
     * @param uid
     * @param wid
     * @param sincest
     * @param untilst
     * @return List<WorkLogWithWnameDto>
     */
    public List<WorkLogWithWnameDto> getWorkLogByWidSettingPeriod(String uid, String wid, String sincest,
            String untilst) {
        ArrayList<WorkLogWithWnameDto> list = new ArrayList<WorkLogWithWnameDto>();

        Date since = new Date();
        Date until = new Date();

        try {
            SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd");

            // String型をDate型に変換する
            since = sdFormat.parse(sincest);
            until = sdFormat.parse(untilst);
        } catch (ParseException e) {
            throw new OtherValidationException(PERSE_EXCEPTION, "create the WorkLog",
                    String.format("ParseException occured")); // 例外を投げる
        }

        Calendar cal = Calendar.getInstance();

        // 1日加算
        cal.setTime(until);
        cal.add(Calendar.DATE, 1);
        until = cal.getTime();

        String wname;

        if (wid.equals("unspecified")) { // 作業が未指定だった場合
            for (WorkLog log : wlr.findByUidAndUntilBetweenOrderBySinceDesc(uid, since, until)) {
                if (!ws.existsWork(log.getWid())) { // 作業が存在しないなら
                    wname = "削除済み";

                    WorkLogWithWnameDto dto = new WorkLogWithWnameDto();

                    list.add(dto.build(log, wname)); // リストに作業名付き作業ログを追加
                }
            }
        } else {
            for (WorkLog log : wlr.findByUidAndWidAndUntilBetweenOrderBySinceDesc(uid, wid, since, until)) {
                // String wid = log.getWid();

                if (!ws.existsWork(wid)) { // 作業が存在しないなら
                    wname = "削除済み";
                } else {
                    wname = ws.getWork(wid).getWname(); // 作業名を所得
                }

                WorkLogWithWnameDto dto = new WorkLogWithWnameDto();

                list.add(dto.build(log, wname)); // リストに作業名付き作業ログを追加
            }
        }

        return list;
    }

    /**
     * 全期間の総合作業時間ランキングを所得
     *
     * @return List<Entry<String, Integer>>
     */
    public List<Entry<String, Integer>> getAllWorkTimeRanking() {
        HashMap<String, Integer> usersworktimes = new HashMap<String, Integer>();

        List<WorkState> users = wsr.findByDiscloseflag(true); // 情報公開しているユーザのリストを所得

        for (WorkState user : users) {
            String uid = user.getUid(); // uidを所得

            List<WorkTimeWithWnameDto> worktimes = this.caluculateAllWorkTime(this.getAllWorkLogByUid(uid)); // 全期間の作業時間を計算

            int allworktime = worktimes.get(worktimes.size() - 1).getSecond(); // 総合作業時間を所得

            usersworktimes.put(ur.findById(uid).get().getNickname(), allworktime); // HashMapに登録
        }

        // 2.Map.Entryのリストを作成する
        List<Entry<String, Integer>> list_entries = new ArrayList<Entry<String, Integer>>(usersworktimes.entrySet());

        // 6. 比較関数Comparatorを使用してMap.Entryの値を比較する（降順）
        Collections.sort(list_entries, new Comparator<Entry<String, Integer>>() {
            // compareを使用して値を比較する
            public int compare(Entry<String, Integer> obj1, Entry<String, Integer> obj2) {
                // 降順
                return obj2.getValue().compareTo(obj1.getValue());
            }
        });

        // HashMap<String, Integer> ranking = new HashMap<String, Integer>();

        // // System.out.println("降順でのソート");
        // // 7. ループで要素順に値を取得する
        // for (Entry<String, Integer> entry : list_entries) {
        // System.out.println(entry.getKey() + " : " + entry.getValue());
        // ranking.put(entry.getKey(), entry.getValue()); // ランキングを作成
        // }

        return list_entries;
    }

    /**
     * 期間を指定して総合作業時間ランキングを所得
     *
     * @param sincest
     * @param untilst
     * @return List<Entry<String, Integer>>
     */
    public List<Entry<String, Integer>> getWorkTimeRankingSettingPeriod(String sincest, String untilst) {
        HashMap<String, Integer> usersworktimes = new HashMap<String, Integer>();

        List<WorkState> users = wsr.findByDiscloseflag(true); // 情報公開しているユーザのリストを所得

        for (WorkState user : users) {
            String uid = user.getUid(); // uidを所得

            List<WorkTimeWithWnameDto> worktimes = this
                    .caluculateAllWorkTime(this.getWorkLogSettingPeriod(uid, sincest, untilst)); // 指定期間の作業時間を計算

            int allworktime = worktimes.get(worktimes.size() - 1).getSecond(); // 総合作業時間を所得

            usersworktimes.put(ur.findById(uid).get().getNickname(), allworktime); // HashMapに登録
        }

        // 2.Map.Entryのリストを作成する
        List<Entry<String, Integer>> list_entries = new ArrayList<Entry<String, Integer>>(usersworktimes.entrySet());

        // 6. 比較関数Comparatorを使用してMap.Entryの値を比較する（降順）
        Collections.sort(list_entries, new Comparator<Entry<String, Integer>>() {
            // compareを使用して値を比較する
            public int compare(Entry<String, Integer> obj1, Entry<String, Integer> obj2) {
                // 降順
                return obj2.getValue().compareTo(obj1.getValue());
            }
        });

        // HashMap<String, Integer> ranking = new HashMap<String, Integer>();

        // // System.out.println("降順でのソート");
        // // 7. ループで要素順に値を取得する
        // for (Entry<String, Integer> entry : list_entries) {
        // System.out.println(entry.getKey() + " : " + entry.getValue());
        // ranking.put(entry.getKey(), entry.getValue()); // ランキングを作成
        // }

        return list_entries;
    }

    /**
     * 昨日のランキング結果をミクちゃんに読み上げさせる
     */
    public void getYesterdayLongestWorkTimeUser() {
        try {
            String url = "http://133.30.159.224:8080/axis2/services/MP3PlayService/play?param0=https://wsapp.cs.kobe-u.ac.jp/webapps/GFSService/stream/famousq/announce.mp3";
            Jsoup.connect(url).post();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(5000); // 10秒(1万ミリ秒)間だけ処理を止める
        } catch (InterruptedException e) {
        }

        Date today = new Date();

        Calendar cal = Calendar.getInstance();

        // 1日減算
        cal.setTime(today);
        cal.add(Calendar.DATE, -1);
        Date yesterday = cal.getTime();

        String yesterdayst = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(yesterday); // Date型をString型に変換

        List<Entry<String, Integer>> list = this.getWorkTimeRankingSettingPeriod(yesterdayst, yesterdayst); // 昨日のランキングを取得

        String announce = "昨日の作業時間ランキングの結果を発表します．"; // アナウンス部分
        int rankcount = 0;

        String first = t.checkExtraWord(list.get(0).getKey()); // 1位のユーザ名
        int firsttime = list.get(0).getValue(); // 1位の作業時間

        if (firsttime != 0) {
            announce = announce + "1位は" + first + "さんで，作業時間は" + firsttime / 3600 + "時間でした．";
            rankcount++;
        } else {
            announce = announce + "昨日は作業をした人がいませんでした．悲しい．";
        }

        // System.out.println(announce);
        String encodedResult = "";
        try {
            encodedResult = URLEncoder.encode(announce, "UTF-8");
            String url = "http://192.168.0.8:8080/axis2/services/MMDAgentProxyService/syncSpeech?text=" + encodedResult;
            Jsoup.connect(url).post();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(1000); // 10秒(1万ミリ秒)間だけ処理を止める
        } catch (InterruptedException e) {
        }

        announce = "";

        String second = t.checkExtraWord(list.get(1).getKey()); // 2位のユーザ名
        int secondtime = list.get(1).getValue(); // 2位の作業時間

        if (secondtime != 0) {
            announce = announce + "続いて，2位は" + second + "さん.";
            rankcount++;
        }

        String third = t.checkExtraWord(list.get(2).getKey()); // 3位のユーザ名
        int thirdtime = list.get(2).getValue(); // 3位の作業時間

        if (thirdtime != 0) {
            announce = announce + "3位は" + third + "さんでした．";
            rankcount++;
        }

        // System.out.println(announce);
        try {
            encodedResult = URLEncoder.encode(announce, "UTF-8");
            String url = "http://192.168.0.8:8080/axis2/services/MMDAgentProxyService/syncSpeech?text=" + encodedResult;
            Jsoup.connect(url).post();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(1000); // 10秒(1万ミリ秒)間だけ処理を止める
        } catch (InterruptedException e) {
        }

        int count = 0;
        List<String> namelist = new ArrayList<String>();

        for (Entry<String, Integer> rank : list) {
            if (rank.getValue() == 0) {
                count++;
                namelist.add(rank.getKey());
            }
        }

        Random random = new Random();
        int randomValue = random.nextInt(count) + rankcount;

        announce = "";

        if (count != 0) {
            announce = announce + "しかし，" + t.checkExtraWord(list.get(randomValue).getKey()) + "さんを筆頭とする" + count
                    + "人のユーザは";
        }

        // System.out.println(announce);
        try {
            encodedResult = URLEncoder.encode(announce, "UTF-8");
            String url = "http://192.168.0.8:8080/axis2/services/MMDAgentProxyService/syncSpeech?text=" + encodedResult;
            Jsoup.connect(url).post();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(1000); // 10秒(1万ミリ秒)間だけ処理を止める
        } catch (InterruptedException e) {
        }

        announce = "";

        announce = announce + "作業の記録がありませんでした．悲しい．みなさん，今日も頑張りましょう．";

        try {
            encodedResult = URLEncoder.encode(announce, "UTF-8");
            String url = "http://192.168.0.8:8080/axis2/services/MMDAgentProxyService/syncSpeech?text=" + encodedResult;
            Jsoup.connect(url).post();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* -------------------- Delete -------------------- */

    /**
     * 作業ログを削除
     *
     * @param lid
     */
    @Transactional
    public void deleteWorkLog(String uid, Long lid) {
        WorkLog log = wlr.findById(lid).orElseThrow(() -> new WorkLogValidationException(WORKLOG_DOES_NOT_EXIST,
                "delete the workLog", String.format("workLog %s does not exist", lid))); // 作業ログを所得、存在しなければ例外を投げる

        String loguid = log.getUid();

        if (!uid.equals(loguid)) { // 消そうとしている作業ログのユーザIDが消そうとしているユーザのものと異なる場合(REST用)
            throw new WorkLogValidationException(UID_DOSE_NOT_MATCH, "delete the workLog",
                    String.format("workLog uid %s dose not match deleting uid %s", loguid, uid)); // 例外を投げる
        }

        // 作業ログに紐づく作業イベントを削除
        wes.deleteWorkEventByEid(log.getStartEvent());
        wes.deleteWorkEventByEid(log.getEndEvent());

        wlr.deleteById(lid); // 作業ログを削除
    }

    /**
     * ユーザに紐づくすべての作業ログを削除
     *
     * @param uid
     */
    public void deleteAllWorkLogByUid(String uid) {
        wlr.deleteByUid(uid);
    }
}
