/*
 * Copyright (c) 2015, 张涛.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kymjs.emoji.model;

import org.kymjs.blog.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Emoji在手机上的显示规则
 * 
 * @author kymjs (http://www.kymjs.com)
 */
public enum DisplayRules {
    // 注意：value不能从0开始，因为0会被库自动设置为删除按钮
    // int type, int value, int resId, String cls
    KJEMOJI0(0, 1, R.drawable.smiley_0, "[微笑]"), KJEMOJI1(0, 1,
            R.drawable.smiley_1, "[撇嘴]"), KJEMOJI2(0, 1, R.drawable.smiley_2,
            "[色]"), KJEMOJI3(0, 1, R.drawable.smiley_3, "[发呆]"), KJEMOJI4(0, 1,
            R.drawable.smiley_4, "[得意]"), KJEMOJI5(0, 1, R.drawable.smiley_5,
            "[流泪]"), KJEMOJI6(0, 1, R.drawable.smiley_6, "[害羞]"), KJEMOJI7(0,
            1, R.drawable.smiley_7, "[闭嘴]"), KJEMOJI8(0, 1,
            R.drawable.smiley_8, "[睡]"), KJEMOJI9(0, 1, R.drawable.smiley_9,
            "[大哭]"), KJEMOJI10(0, 1, R.drawable.smiley_10, "[尴尬]"), KJEMOJI11(
            0, 1, R.drawable.smiley_11, "[发怒]"), KJEMOJI12(0, 1,
            R.drawable.smiley_12, "[调皮]"), KJEMOJI13(0, 1,
            R.drawable.smiley_13, "[呲牙]"), KJEMOJI14(0, 1,
            R.drawable.smiley_14, "[惊讶]"), KJEMOJI15(0, 1,
            R.drawable.smiley_15, "[难过]"), KJEMOJI16(0, 1,
            R.drawable.smiley_16, "[酷]"), KJEMOJI17(0, 1, R.drawable.smiley_17,
            "[冷汗]"), KJEMOJI18(0, 1, R.drawable.smiley_18, "[抓狂]"), KJEMOJI19(
            0, 1, R.drawable.smiley_19, "[吐]"), KJEMOJI20(0, 1,
            R.drawable.smiley_20, "[偷笑]"), KJEMOJI21(0, 1,
            R.drawable.smiley_21, "[可爱]"), KJEMOJI22(0, 1,
            R.drawable.smiley_22, "[白眼]"), KJEMOJI23(0, 1,
            R.drawable.smiley_23, "[傲慢]"), KJEMOJI24(0, 1,
            R.drawable.smiley_24, "[饥饿]"), KJEMOJI25(0, 1,
            R.drawable.smiley_25, "[困]"), KJEMOJI26(0, 1, R.drawable.smiley_26,
            "[惊恐]"), KJEMOJI27(0, 1, R.drawable.smiley_27, "[流汗]"), KJEMOJI28(
            0, 1, R.drawable.smiley_28, "[憨笑]"), KJEMOJI29(0, 1,
            R.drawable.smiley_29, "[大兵]"), KJEMOJI30(0, 1,
            R.drawable.smiley_30, "[奋斗]"), KJEMOJI31(0, 1,
            R.drawable.smiley_31, "[咒骂]"), KJEMOJI32(0, 1,
            R.drawable.smiley_32, "[疑问]"), KJEMOJI33(0, 1,
            R.drawable.smiley_33, "[嘘]"), KJEMOJI34(0, 1, R.drawable.smiley_34,
            "[晕]"), KJEMOJI35(0, 1, R.drawable.smiley_35, "[折磨]"), KJEMOJI36(0,
            1, R.drawable.smiley_36, "[衰]"), KJEMOJI37(0, 1,
            R.drawable.smiley_37, "[骷髅]"), KJEMOJI38(0, 1,
            R.drawable.smiley_38, "[敲打]"), KJEMOJI39(0, 1,
            R.drawable.smiley_39, "[再见]"), KJEMOJI40(0, 1,
            R.drawable.smiley_40, "[擦汗]"), KJEMOJI41(0, 1,
            R.drawable.smiley_41, "[抠鼻]"), KJEMOJI42(0, 1,
            R.drawable.smiley_42, "[鼓掌]"), KJEMOJI43(0, 1,
            R.drawable.smiley_43, "[糗大了]"), KJEMOJI44(0, 1,
            R.drawable.smiley_44, "[坏笑]"), KJEMOJI45(0, 1,
            R.drawable.smiley_45, "[左哼哼]"), KJEMOJI46(0, 1,
            R.drawable.smiley_46, "[右哼哼]"), KJEMOJI47(0, 1,
            R.drawable.smiley_47, "[哈欠]"), KJEMOJI48(0, 1,
            R.drawable.smiley_48, "[鄙视]"), KJEMOJI49(0, 1,
            R.drawable.smiley_49, "[委屈]"), KJEMOJI50(0, 1,
            R.drawable.smiley_50, "[快哭了]"), KJEMOJI51(0, 1,
            R.drawable.smiley_51, "[阴险]"), KJEMOJI52(0, 1,
            R.drawable.smiley_52, "[亲亲]"), KJEMOJI53(0, 1,
            R.drawable.smiley_53, "[吓]"), KJEMOJI54(0, 1, R.drawable.smiley_54,
            "[可怜]"), KJEMOJI55(0, 1, R.drawable.smiley_55, "[菜刀]"), KJEMOJI56(
            0, 1, R.drawable.smiley_56, "[西瓜]"), KJEMOJI57(0, 1,
            R.drawable.smiley_57, "[啤酒]"), KJEMOJI58(0, 1,
            R.drawable.smiley_58, "[篮球]"), KJEMOJI59(0, 1,
            R.drawable.smiley_59, "[乒乓]"), KJEMOJI60(0, 1,
            R.drawable.smiley_60, "[咖啡]"), KJEMOJI61(0, 1,
            R.drawable.smiley_61, "[饭]"), KJEMOJI62(0, 1, R.drawable.smiley_62,
            "[猪头]"), KJEMOJI63(0, 1, R.drawable.smiley_63, "[玫瑰]"), KJEMOJI64(
            0, 1, R.drawable.smiley_64, "[凋谢]"), KJEMOJI65(0, 1,
            R.drawable.smiley_65, "[嘴唇]"), KJEMOJI66(0, 1,
            R.drawable.smiley_66, "[爱心]"), KJEMOJI67(0, 1,
            R.drawable.smiley_67, "[心碎]"), KJEMOJI68(0, 1,
            R.drawable.smiley_68, "[蛋糕]"), KJEMOJI69(0, 1,
            R.drawable.smiley_69, "[闪电]"), KJEMOJI70(0, 1,
            R.drawable.smiley_70, "[炸弹]"), KJEMOJI71(0, 1,
            R.drawable.smiley_71, "[刀]"), KJEMOJI72(0, 1, R.drawable.smiley_72,
            "[足球]"), KJEMOJI73(0, 1, R.drawable.smiley_73, "[瓢虫]"), KJEMOJI74(
            0, 1, R.drawable.smiley_74, "[便便]"), KJEMOJI75(0, 1,
            R.drawable.smiley_75, "[月亮]"), KJEMOJI76(0, 1,
            R.drawable.smiley_76, "[太阳]"), KJEMOJI77(0, 1,
            R.drawable.smiley_77, "[礼物]"), KJEMOJI78(0, 1,
            R.drawable.smiley_78, "[拥抱]"), KJEMOJI79(0, 1,
            R.drawable.smiley_79, "[强]"), KJEMOJI80(0, 1, R.drawable.smiley_80,
            "[弱]"), KJEMOJI81(0, 1, R.drawable.smiley_81, "[握手]"), KJEMOJI82(0,
            1, R.drawable.smiley_82, "[胜利]"), KJEMOJI83(0, 1,
            R.drawable.smiley_83, "[抱拳]"), KJEMOJI84(0, 1,
            R.drawable.smiley_84, "[勾引]"), KJEMOJI85(0, 1,
            R.drawable.smiley_85, "[拳头]"), KJEMOJI86(0, 1,
            R.drawable.smiley_86, "[差劲]"), KJEMOJI87(0, 1,
            R.drawable.smiley_87, "[爱你]"), KJEMOJI88(0, 1,
            R.drawable.smiley_88, "[NO]"), KJEMOJI89(0, 1,
            R.drawable.smiley_89, "[OK]"), KJEMOJI90(0, 1,
            R.drawable.smiley_90, "[爱情]"), KJEMOJI91(0, 1,
            R.drawable.smiley_91, "[飞吻]"), KJEMOJI92(0, 1,
            R.drawable.smiley_92, "[跳跳]"), KJEMOJI93(0, 1,
            R.drawable.smiley_93, "[发抖]"), KJEMOJI94(0, 1,
            R.drawable.smiley_94, "[怄火]"), KJEMOJI95(0, 1,
            R.drawable.smiley_95, "[转圈]"), KJEMOJI96(0, 1,
            R.drawable.smiley_96, "[磕头]"), KJEMOJI97(0, 1,
            R.drawable.smiley_97, "[回头]"), KJEMOJI98(0, 1,
            R.drawable.smiley_98, "[跳绳]"), KJEMOJI99(0, 1,
            R.drawable.smiley_99, "[投降]"), KJEMOJI100(0, 1,
            R.drawable.smiley_100, "[激动]"), KJEMOJI101(0, 1,
            R.drawable.smiley_101, "[乱舞]"), KJEMOJI102(0, 1,
            R.drawable.smiley_102, "[献吻]"), KJEMOJI103(0, 1,
            R.drawable.smiley_103, "[左太极]"), KJEMOJI104(0, 1,
            R.drawable.smiley_104, "[右太极]"),

    GITHUB0(1, 1, R.drawable.bowtie, "[bowtie]"),

    GITHUB1(1, 1, R.drawable.smile, "[smile]"),

    GITHUB2(1, 1, R.drawable.laughing, "[laughing]"),

    GITHUB3(1, 1, R.drawable.blush, "[blush]"),

    GITHUB4(1, 1, R.drawable.smiley, "[smiley]"),

    GITHUB5(1, 1, R.drawable.relaxed, "[relaxed]"),

    GITHUB6(1, 1, R.drawable.smirk, "[smirk]"),

    GITHUB7(1, 1, R.drawable.heart_eyes, "[heart_eyes]"),

    GITHUB8(1, 1, R.drawable.kissing_heart, "[kissing_heart]"),

    GITHUB9(1, 1, R.drawable.kissing_closed_eyes, "[kissing_closed_eyes]"),

    GITHUB10(1, 1, R.drawable.flushed, "[flushed]"),

    GITHUB11(1, 1, R.drawable.relieved, "[relieved]"),

    GITHUB12(1, 1, R.drawable.satisfied, "[satisfied]"),

    GITHUB13(1, 1, R.drawable.grin, "[grin]"),

    GITHUB14(1, 1, R.drawable.wink, "[wink]"),

    GITHUB15(1, 1, R.drawable.stuck_out_tongue_winking_eye,
            "[stuck_out_tongue_winking_eye]"),

    GITHUB16(1, 1, R.drawable.stuck_out_tongue_closed_eyes,
            "[stuck_out_tongue_closed_eyes]"),

    GITHUB17(1, 1, R.drawable.grinning, "[grinning]"),

    GITHUB18(1, 1, R.drawable.kissing, "[kissing]"),

    GITHUB19(1, 1, R.drawable.kissing_smiling_eyes, "[kissing_smiling_eyes]"),

    GITHUB20(1, 1, R.drawable.stuck_out_tongue, "[stuck_out_tongue]"),

    GITHUB21(1, 1, R.drawable.sleeping, "[sleeping]"),

    GITHUB22(1, 1, R.drawable.worried, "[worried]"),

    GITHUB23(1, 1, R.drawable.frowning, "[frowning]"),

    GITHUB24(1, 1, R.drawable.anguished, "[anguished]"),

    GITHUB25(1, 1, R.drawable.open_mouth, "[open_mouth]"),

    GITHUB26(1, 1, R.drawable.grimacing, "[grimacing]"),

    GITHUB27(1, 1, R.drawable.confused, "[confused]"),

    GITHUB28(1, 1, R.drawable.hushed, "[hushed]"),

    GITHUB29(1, 1, R.drawable.expressionless, "[expressionless]"),

    GITHUB30(1, 1, R.drawable.unamused, "[unamused]"),

    GITHUB31(1, 1, R.drawable.sweat_smile, "[sweat_smile]"),

    GITHUB32(1, 1, R.drawable.sweat, "[sweat]"),

    GITHUB33(1, 1, R.drawable.disappointed_relieved, "[disappointed_relieved]"),

    GITHUB34(1, 1, R.drawable.weary, "[weary]"),

    GITHUB35(1, 1, R.drawable.pensive, "[pensive]"),

    GITHUB36(1, 1, R.drawable.disappointed, "[disappointed]"),

    GITHUB37(1, 1, R.drawable.confounded, "[confounded]"),

    GITHUB38(1, 1, R.drawable.fearful, "[fearful]"),

    GITHUB39(1, 1, R.drawable.cold_sweat, "[cold_sweat]"),

    GITHUB40(1, 1, R.drawable.persevere, "[persevere]"),

    GITHUB41(1, 1, R.drawable.cry, "[cry]"),

    GITHUB42(1, 1, R.drawable.sob, "[sob]"),

    GITHUB43(1, 1, R.drawable.joy, "[joy]"),

    GITHUB44(1, 1, R.drawable.astonished, "[astonished]"),

    GITHUB45(1, 1, R.drawable.scream, "[scream]"),

    GITHUB46(1, 1, R.drawable.neckbeard, "[neckbeard]"),

    GITHUB47(1, 1, R.drawable.tired_face, "[tired_face]"),

    GITHUB48(1, 1, R.drawable.angry, "[angry]"),

    GITHUB49(1, 1, R.drawable.rage, "[rage]"),

    GITHUB50(1, 1, R.drawable.triumph, "[triumph]"),

    GITHUB51(1, 1, R.drawable.sleepy, "[sleepy]"),

    GITHUB52(1, 1, R.drawable.yum, "[yum]"),

    GITHUB53(1, 1, R.drawable.mask, "[mask]"),

    GITHUB54(1, 1, R.drawable.sunglasses, "[sunglasses]"),

    GITHUB55(1, 1, R.drawable.dizzy_face, "[dizzy_face]"),

    GITHUB56(1, 1, R.drawable.imp, "[imp]"),

    GITHUB57(1, 1, R.drawable.smiling_imp, "[smiling_imp]"),

    GITHUB58(1, 1, R.drawable.neutral_face, "[neutral_face]"),

    GITHUB59(1, 1, R.drawable.no_mouth, "[no_mouth]"),

    GITHUB60(1, 1, R.drawable.innocent, "[innocent]"),

    GITHUB61(1, 1, R.drawable.alien, "[alien]"),

    GITHUB62(1, 1, R.drawable.yellow_heart, "[yellow_heart]"),

    GITHUB63(1, 1, R.drawable.blue_heart, "[blue_heart]"),

    GITHUB64(1, 1, R.drawable.purple_heart, "[purple_heart]"),

    GITHUB65(1, 1, R.drawable.heart, "[heart]"),

    GITHUB66(1, 1, R.drawable.green_heart, "[green_heart]"),

    GITHUB67(1, 1, R.drawable.broken_heart, "[broken_heart]"),

    GITHUB68(1, 1, R.drawable.heartbeat, "[heartbeat]"),

    GITHUB69(1, 1, R.drawable.heartpulse, "[heartpulse]"),

    GITHUB70(1, 1, R.drawable.two_hearts, "[two_hearts]"),

    GITHUB71(1, 1, R.drawable.revolving_hearts, "[revolving_hearts]"),

    GITHUB72(1, 1, R.drawable.cupid, "[cupid]"),

    GITHUB73(1, 1, R.drawable.sparkling_heart, "[sparkling_heart]"),

    GITHUB74(1, 1, R.drawable.sparkles, "[sparkles]"),

    GITHUB75(1, 1, R.drawable.star, "[star]"),

    GITHUB76(1, 1, R.drawable.star2, "[star2]"),

    GITHUB77(1, 1, R.drawable.dizzy, "[dizzy]"),

    GITHUB78(1, 1, R.drawable.boom, "[boom]"),

    GITHUB79(1, 1, R.drawable.collision, "[collision]"),

    GITHUB80(1, 1, R.drawable.anger, "[anger]"),

    GITHUB81(1, 1, R.drawable.exclamation, "[exclamation]"),

    GITHUB82(1, 1, R.drawable.question, "[question]"),

    GITHUB83(1, 1, R.drawable.grey_exclamation, "[grey_exclamation]"),

    GITHUB84(1, 1, R.drawable.grey_question, "[grey_question]"),

    GITHUB85(1, 1, R.drawable.zzz, "[zzz]"),

    GITHUB86(1, 1, R.drawable.dash, "[dash]"),

    GITHUB87(1, 1, R.drawable.sweat_drops, "[sweat_drops]"),

    GITHUB88(1, 1, R.drawable.notes, "[notes]"),

    GITHUB89(1, 1, R.drawable.musical_note, "[musical_note]"),

    GITHUB90(1, 1, R.drawable.fire, "[fire]"),

    GITHUB91(1, 1, R.drawable.hankey, "[hankey]"),

    GITHUB92(1, 1, R.drawable.poop, "[poop]"),

    GITHUB93(1, 1, R.drawable.shit, "[shit]"),

    GITHUB94(1, 1, R.drawable.thumbsup, "[+1]"),

    GITHUB95(1, 1, R.drawable.thumbsup, "[thumbsup]"),

    GITHUB96(1, 1, R.drawable.the_1, "[-1]"),

    GITHUB97(1, 1, R.drawable.thumbsdown, "[thumbsdown]"),

    GITHUB98(1, 1, R.drawable.ok_hand, "[ok_hand]"),

    GITHUB99(1, 1, R.drawable.punch, "[punch]"),

    GITHUB100(1, 1, R.drawable.facepunch, "[facepunch]"),

    GITHUB101(1, 1, R.drawable.fist, "[fist]"),

    GITHUB102(1, 1, R.drawable.v, "[v]"),

    GITHUB103(1, 1, R.drawable.wave, "[wave]"),

    GITHUB104(1, 1, R.drawable.hand, "[hand]"),

    GITHUB105(1, 1, R.drawable.raised_hand, "[raised_hand]"),

    GITHUB106(1, 1, R.drawable.open_hands, "[open_hands]"),

    GITHUB107(1, 1, R.drawable.point_up, "[point_up]"),

    GITHUB108(1, 1, R.drawable.point_down, "[point_down]"),

    GITHUB109(1, 1, R.drawable.point_left, "[point_left]"),

    GITHUB110(1, 1, R.drawable.point_right, "[point_right]"),

    GITHUB111(1, 1, R.drawable.raised_hands, "[raised_hands]"),

    GITHUB112(1, 1, R.drawable.pray, "[pray]"),

    GITHUB113(1, 1, R.drawable.point_up_2, "[point_up_2]"),

    GITHUB114(1, 1, R.drawable.clap, "[clap]"),

    GITHUB115(1, 1, R.drawable.muscle, "[muscle]"),

    GITHUB116(1, 1, R.drawable.metal, "[metal]"),

    GITHUB117(1, 1, R.drawable.fu, "[fu]"),

    GITHUB118(1, 1, R.drawable.walking, "[walking]"),

    GITHUB119(1, 1, R.drawable.runner, "[runner]"),

    GITHUB120(1, 1, R.drawable.running, "[running]"),

    GITHUB121(1, 1, R.drawable.couple, "[couple]"),

    GITHUB122(1, 1, R.drawable.family, "[family]"),

    GITHUB123(1, 1, R.drawable.two_men_holding_hands, "[two_men_holding_hands]"),

    GITHUB124(1, 1, R.drawable.two_women_holding_hands,
            "[two_women_holding_hands]"),

    GITHUB125(1, 1, R.drawable.dancer, "[dancer]"),

    GITHUB126(1, 1, R.drawable.dancers, "[dancers]"),

    GITHUB127(1, 1, R.drawable.ok_woman, "[ok_woman]"),

    GITHUB128(1, 1, R.drawable.no_good, "[no_good]"),

    GITHUB129(1, 1, R.drawable.information_desk_person,
            "[information_desk_person]"),

    GITHUB130(1, 1, R.drawable.raising_hand, "[raising_hand]"),

    GITHUB131(1, 1, R.drawable.bride_with_veil, "[bride_with_veil]"),

    GITHUB132(1, 1, R.drawable.person_with_pouting_face,
            "[person_with_pouting_face]"),

    GITHUB133(1, 1, R.drawable.person_frowning, "[person_frowning]"),

    GITHUB134(1, 1, R.drawable.bow, "[bow]"),

    GITHUB135(1, 1, R.drawable.couplekiss, "[couplekiss]"),

    GITHUB136(1, 1, R.drawable.couple_with_heart, "[couple_with_heart]"),

    GITHUB137(1, 1, R.drawable.massage, "[massage]"),

    GITHUB138(1, 1, R.drawable.haircut, "[haircut]"),

    GITHUB139(1, 1, R.drawable.nail_care, "[nail_care]"),

    GITHUB140(1, 1, R.drawable.boy, "[boy]"),

    GITHUB141(1, 1, R.drawable.girl, "[girl]"),

    GITHUB142(1, 1, R.drawable.woman, "[woman]"),

    GITHUB143(1, 1, R.drawable.man, "[man]"),

    GITHUB144(1, 1, R.drawable.baby, "[baby]"),

    GITHUB145(1, 1, R.drawable.older_woman, "[older_woman]"),

    GITHUB146(1, 1, R.drawable.older_man, "[older_man]"),

    GITHUB147(1, 1, R.drawable.person_with_blond_hair,
            "[person_with_blond_hair]"),

    GITHUB148(1, 1, R.drawable.man_with_gua_pi_mao, "[man_with_gua_pi_mao]"),

    GITHUB149(1, 1, R.drawable.man_with_turban, "[man_with_turban]"),

    GITHUB150(1, 1, R.drawable.construction_worker, "[construction_worker]"),

    GITHUB151(1, 1, R.drawable.cop, "[cop]"),

    GITHUB152(1, 1, R.drawable.angel, "[angel]"),

    GITHUB153(1, 1, R.drawable.princess, "[princess]"),

    GITHUB154(1, 1, R.drawable.smiley_cat, "[smiley_cat]"),

    GITHUB155(1, 1, R.drawable.smile_cat, "[smile_cat]"),

    GITHUB156(1, 1, R.drawable.heart_eyes_cat, "[heart_eyes_cat]"),

    GITHUB157(1, 1, R.drawable.kissing_cat, "[kissing_cat]"),

    GITHUB158(1, 1, R.drawable.smirk_cat, "[smirk_cat]"),

    GITHUB159(1, 1, R.drawable.scream_cat, "[scream_cat]"),

    GITHUB160(1, 1, R.drawable.crying_cat_face, "[crying_cat_face]"),

    GITHUB161(1, 1, R.drawable.joy_cat, "[joy_cat]"),

    GITHUB162(1, 1, R.drawable.pouting_cat, "[pouting_cat]"),

    GITHUB163(1, 1, R.drawable.japanese_ogre, "[japanese_ogre]"),

    GITHUB164(1, 1, R.drawable.japanese_goblin, "[japanese_goblin]"),

    GITHUB165(1, 1, R.drawable.see_no_evil, "[see_no_evil]"),

    GITHUB166(1, 1, R.drawable.hear_no_evil, "[hear_no_evil]"),

    GITHUB167(1, 1, R.drawable.speak_no_evil, "[speak_no_evil]"),

    GITHUB168(1, 1, R.drawable.guardsman, "[guardsman]"),

    GITHUB169(1, 1, R.drawable.skull, "[skull]"),

    GITHUB170(1, 1, R.drawable.feet, "[feet]"),

    GITHUB171(1, 1, R.drawable.lips, "[lips]"),

    GITHUB172(1, 1, R.drawable.kiss, "[kiss]"),

    GITHUB173(1, 1, R.drawable.droplet, "[droplet]"),

    GITHUB174(1, 1, R.drawable.ear, "[ear]"),

    GITHUB175(1, 1, R.drawable.eyes, "[eyes]"),

    GITHUB176(1, 1, R.drawable.nose, "[nose]"),

    GITHUB177(1, 1, R.drawable.tongue, "[tongue]"),

    GITHUB178(1, 1, R.drawable.love_letter, "[love_letter]"),

    GITHUB179(1, 1, R.drawable.bust_in_silhouette, "[bust_in_silhouette]"),

    GITHUB180(1, 1, R.drawable.busts_in_silhouette, "[busts_in_silhouette]"),

    GITHUB181(1, 1, R.drawable.speech_balloon, "[speech_balloon]"),

    GITHUB182(1, 1, R.drawable.thought_balloon, "[thought_balloon]"),

    GITHUB183(1, 1, R.drawable.feelsgood, "[feelsgood]"),

    GITHUB184(1, 1, R.drawable.finnadie, "[finnadie]"),

    GITHUB185(1, 1, R.drawable.goberserk, "[goberserk]"),

    GITHUB186(1, 1, R.drawable.godmode, "[godmode]"),

    GITHUB187(1, 1, R.drawable.hurtrealbad, "[hurtrealbad]"),

    GITHUB188(1, 1, R.drawable.rage1, "[rage1]"),

    GITHUB189(1, 1, R.drawable.rage2, "[rage2]"),

    GITHUB190(1, 1, R.drawable.rage3, "[rage3]"),

    GITHUB191(1, 1, R.drawable.rage4, "[rage4]"),

    GITHUB192(1, 1, R.drawable.suspect, "[suspect]"),

    GITHUB193(1, 1, R.drawable.trollface, "[trollface]");

    /********************************* 操作 **************************************/
    private String emojiStr;
    private int value;
    private int resId;
    private int type;
    private static Map<String, Integer> sEmojiMap;

    private DisplayRules(int type, int value, int resId, String cls) {
        this.type = type;
        this.emojiStr = cls;
        this.value = value;
        this.resId = resId;
    }

    public String getEmojiStr() {
        return emojiStr;
    }

    public int getValue() {
        return value;
    }

    public int getResId() {
        return resId;
    }

    public int getType() {
        return type;
    }

    private static Emojicon getEmojiFromEnum(DisplayRules data) {
        return new Emojicon(data.getResId(), data.getValue(),
                data.getEmojiStr());
    }

    public static Emojicon getEmojiFromRes(int resId) {
        for (DisplayRules data : values()) {
            if (data.getResId() == resId) {
                return getEmojiFromEnum(data);
            }
        }
        return null;
    }

    public static Emojicon getEmojiFromValue(int value) {
        for (DisplayRules data : values()) {
            if (data.getValue() == value) {
                return getEmojiFromEnum(data);
            }
        }
        return null;
    }

    public static Emojicon getEmojiFromName(String emojiStr) {
        for (DisplayRules data : values()) {
            if (data.getEmojiStr().equals(emojiStr)) {
                return getEmojiFromEnum(data);
            }
        }
        return null;
    }

    /**
     * 提高效率，忽略线程安全
     */
    public static Map<String, Integer> getMapAll() {
        if (sEmojiMap == null) {
            sEmojiMap = new HashMap<String, Integer>();
            for (DisplayRules data : values()) {
                sEmojiMap.put(data.getEmojiStr(), data.getResId());
            }
        }
        return sEmojiMap;
    }

    public static List<Emojicon> getAllByType(int type) {
        List<Emojicon> datas = new ArrayList<Emojicon>(values().length);
        for (DisplayRules data : values()) {
            if (data.getType() == type) {
                datas.add(getEmojiFromEnum(data));
            }
        }
        return datas;
    }
}
