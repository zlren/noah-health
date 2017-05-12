package com.yhch.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/test")
public class TestController {

// 	Logger logger = Logger.getLogger(TestController.class);
//
// 	@Autowired
// 	private UserService userService;
//
// 	@Autowired
// 	private MemberService memberService;
//
// 	@Autowired
// 	private ExpertService expertService;
//
// 	@Autowired
// 	private MFeedbackService mFeedbackService;
//
// 	@Autowired
// 	private MHealthService mHealthService;
//
// 	@Autowired
// 	private MConsultationService mConsultationService;
//
// 	@Autowired
// 	private MAccountService mAccountService;
//
// 	@Autowired
// 	private TableHyService mtHyTableService;
//
// 	@RequestMapping("/info")
// 	public String showUserInfo() {
//
// 		// Member memberInfo = memberService.getMemberById("1");
// 		// System.out.println(memberInfo);
//
// 		// List<Member> l = memberService.getMembersByPage(5);
// 		// Iterator<Member> i = l.iterator();
// 		//
// 		// while(i.hasNext()) {
// 		// System.out.println(i.next());
// 		// }
//
// 		// Map<String, Object> e = new HashMap<String, Object>(2);
// 		// e = expertService.getExpertsByPage(1);
// 		//
// 		// //采用Iterator遍历HashMap
// 		// Iterator it = e.keySet().iterator();
// 		// while(it.hasNext()) {
// 		// String key = (String)it.next();
// 		// System.out.println("key:" + key);
// 		// System.out.println("value:" + e.get(key));
// 		// }
//
// 		// Map<String, Object> e = new HashMap<String, Object>(2);
// 		// e = expertService.getExpertsByName("1");
// 		//
// 		// //采用Iterator遍历HashMap
// 		// Iterator it = e.keySet().iterator();
// 		// while(it.hasNext()) {
// 		// String key = (String)it.next();
// 		// System.out.println("key:" + key);
// 		// System.out.println("value:" + e.get(key));
// 		// }
//
// 		// Expert expert = new Expert();
// 		// for(int i = 12; i <= 34; i++) {
// 		// expert.setId(14);
// 		// expert.setName("zlzlzl");
// 		// expert.setJobTitle("主治医师");
// 		// expert.setHospital("北医三院");
// 		// expert.setDepartment("外科");
// 		// expert.setVisitTime("周末");
// 		// expert.setChannel("黄牛");
// 		// expert.setGoodat("外科");
// 		// expert.setCost("一万");
// 		// expert.setNote("不错");
// 		// expert.setInputer("zlren");
// 		//
// 		// expertService.insert(expert);
// 		// }
//
// 		// expertService.deleteById(2);
//
// 		// MFeedback mFeedback = new MFeedback();
// 		// for(int i = 1; i < 13; i++) {
// 		// mFeedback.setFeedbackId(16);
// 		// mFeedback.setId(333);
// 		// mFeedback.setTime(new Date());
// 		// mFeedback.setContent("哈哈哈update");
// 		// mFeedback.setTel("12345678910");
// 		// mFeedback.setNote("反馈记录备注");
//
// 		// mFeedbackService.insert(mFeedback);
// 		// mFeedbackService.updateById(mFeedback);
// 		// }
//
// 		// Map<String, Object> mFeedbackMap =
// 		// mFeedbackService.getMFeedbacksByIdByPage(2, 1);
// 		//
// 		// //采用Iterator遍历HashMap
// 		// Iterator<String> it = mFeedbackMap.keySet().iterator();
// 		// while(it.hasNext()) {
// 		// String key = (String)it.next();
// 		// System.out.println("key:" + key);
// 		// System.out.println("value:" + mFeedbackMap.get(key));
// 		// }
//
// 		// Map<String, Object> mHealthMap = mHealthService.getMHealthsByPage(2);
// 		//
// 		// //采用Iterator遍历HashMap
// 		// Iterator<String> it = mHealthMap.keySet().iterator();
// 		// while(it.hasNext()) {
// 		// String key = (String)it.next();
// 		// System.out.println("key:" + key);
// 		// System.out.println("value:" + mHealthMap.get(key));
// 		// }
//
// 		// MHealth mHealth = mHealthService.getMHealthById(9999);
// 		// System.out.println(mHealth);
//
// 		// MConsultation mConsultation =
// 		// mConsultationService.getMConsultationById(1111);
// 		// System.out.println(mConsultation);
//
// 		// Map<String, Object> mConsultationMap =
// 		// mConsultationService.getMConsultationsByPage(1);
// 		//
// 		// //采用Iterator遍历HashMap
// 		// Iterator<String> it = mConsultationMap.keySet().iterator();
// 		// while(it.hasNext()) {
// 		// String key = (String)it.next();
// 		// System.out.println("key:" + key);
// 		// System.out.println("value:" + mConsultationMap.get(key));
// 		// }
//
// 		Map<String, Float> balance = mAccountService.getMAccountBalance(1);
//
// 		System.out.println(balance.get("balance"));
//
// 		return "/test";
// 	}
//
// 	@RequestMapping("/member/{pageNumber}")
// 	@ResponseBody
// 	public Map<String, Object> testForMember(@PathVariable("pageNumber") Integer pageNumber) {
// 		return memberService.getMembersByPage(pageNumber);
// 	}
//
// 	@RequestMapping("/account")
// 	@ResponseBody
// 	public Map<String, Object> testForMAccount() {
//
// 		Map<String, Float> balance = mAccountService.getMAccountBalance(1);
// 		System.out.println("balance是" + balance.get("balance"));
//
// 		Map<String, Object> maps = mAccountService.getMAccountsByPageByMemberId(1, 1);
// 		Iterator<String> it = maps.keySet().iterator();
// 		while (it.hasNext()) {
// 			String key = (String) it.next();
// 			System.out.println("key:" + key);
// 			System.out.println("value:" + maps.get(key));
// 		}
//
// 		return maps;
// 	}
//
// 	@RequestMapping("/consultation")
// 	@ResponseBody
// 	public Map<String, Object> testForMConsultation() {
//
// 		Map<String, Object> maps = mConsultationService.getMConsultationsByPageByMemberId(1111, 1);
// 		Iterator<String> it = maps.keySet().iterator();
// 		while (it.hasNext()) {
// 			String key = (String) it.next();
// 			System.out.println("key:" + key);
// 			System.out.println("value:" + maps.get(key));
// 		}
//
// 		return maps;
// 	}
//
// 	@RequestMapping("/feedback/{pageNumber}")
// 	@ResponseBody
// 	public Map<String, Object> testForMFeedback(@PathVariable("pageNumber") Integer pageNumber) {
//
// 		Map<String, Object> maps = mFeedbackService.getMFeedbacksByPage(pageNumber);
// 		// Map<String, Object> maps =
// 		// mFeedbackService.getMFeedbacksByIdByPage(1, 1);
// 		// Map<String, Object> maps =
// 		// mFeedbackService.getMFeedbacksByPageByMemberId(1, 1);
//
// 		return maps;
// 	}
//
// 	@RequestMapping("/health")
// 	@ResponseBody
// 	public Map<String, Object> testForMHealth() {
//
// 		Map<String, Object> map = mHealthService.getMHealthsByPage(2);
//
// 		return map;
// 	}
//
// 	@RequestMapping("/table/{pageNumber}")
// 	@ResponseBody
// 	public Map<String, Object> testForHyXxg(@PathVariable("pageNumber") Integer pageNumber) {
//
// 		// HyXxg hyXxg = new HyXxg();
// 		// hyXxg.setMemberId(1188);
// 		// hyXxg.setTime(new Date());
// 		//
// 		// hyXxg.setGYSZ(1.7);
// 		// hyXxg.setZDGC(5.7);
// 		// hyXxg.setGMDZDBDGC(1.6);
// 		// hyXxg.setFGMDZDBDGC(4.2);
// 		// hyXxg.setDMDZDBDGC(3.4);
// 		// hyXxg.setTCHDLC(5.0); // 参考值写着<5，实际上赋值的时候要写成5.0
// 		// hyXxg.setZZDBA(1.6);
// 		// hyXxg.setZZDBB(1.2);
// 		// hyXxg.setZZDBE(5.0);
// 		// hyXxg.setXQZDBA(75);
// 		// hyXxg.setTXBPAS(20.0);
// 		// hyXxg.setYS(14.4);
// 		// hyXxg.setWSSB12(1059);
// 		// hyXxg.setCFYDBCRP(0.3);
// 		// hyXxg.setJSJMCKP(200);
// 		// hyXxg.setJSJMTGMCKMB(25);
// 		// hyXxg.setJSJMTGMMBDL(6.5);
// 		// hyXxg.setRSTQMLDH(250);
// 		// hyXxg.setAQDSTQM(220);
// 		// hyXxg.setJHDBMB(75);
// 		// hyXxg.setJGDBL(0.1);
// 		// hyXxg.setJGDBT(0.1);
// 		// hyXxg.setNLNTQT(150);
// 		//
// 		//
// 		// int resultInt = mHyXxgService.insert(hyXxg);
// 		// logger.info("返回值为:" + resultInt);
// 		// logger.info("主键id是:" + hyXxg.getId());
//
// 		// Map<String, Object> result =
// 		// mtHyTableService.getHyXxgsByPage(pageNumber);
// 		// Map<String, Object> result =
// 		// mtHyTableService.getHyTnbsByMemberIdByPage(111, pageNumber);
// 		// Map<String, Object> result =
// 		// mtHyTableService.getHyFbjcwsByMemberIdByPage(111, pageNumber);
// 		// Map<String, Object> result =
// 		// mtHyTableService.getHySzmnsByMemberIdByPage(112, pageNumber);
// 		// Map<String, Object> result =
// 		// mtHyTableService.getHyNkfksByMemberIdByPage(112, pageNumber);
// 		// Map<String, Object> result =
// 		// mtHyTableService.getHyGgjszsByMemberIdByPage(112, pageNumber);
// //		Map<String, Object> result = mtHyTableService.getHyJzxdfmwsByMemberIdByPage(112, pageNumber);
// //		 Map<String, Object> result =
// //		 mtHyTableService.getHyZlsByMemberIdByPage(112, pageNumber);
// //		 Map<String, Object> result =
// //		 mtHyTableService.getHyFsmysByMemberIdByPage(112, pageNumber);
// //		 Map<String, Object> result =
// //		 mtHyTableService.getHyXyxtsByMemberIdByPage(112, pageNumber);
// //		 Map<String, Object> result =
// //		 mtHyTableService.getHyCrbsByMemberIdByPage(112, pageNumber);
// 		 Map<String, Object> result =
// 		 mtHyTableService.getHyYjksByMemberIdByPage(111, pageNumber);
//
// 		return result;
// 	}
//
// 	 @RequestMapping("/user")
// 	 @ResponseBody
// 	 public User testForUser() {
// 		 User user = userService.getUserByUsername("no");
// 		 if (user == null) {
// 			 logger.info("null!!!");
// 		 }
// 		 return user;
// 	 }
}
