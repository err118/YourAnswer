package ssm.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import ssm.pojo.Answer;
import ssm.pojo.Question;
import ssm.pojo.User;
import ssm.service.AnswerService;
import ssm.service.QuestionService;
import ssm.service.UserService;

@Controller
@RequestMapping("")
public class AnswerController {

	private QuestionService questionService;
	private AnswerService answerService;
	private UserService userService;

	@Autowired
	public void setQuestionService(QuestionService questionService) {
		this.questionService = questionService;
	}
	@Autowired
	public void setAnswerService(AnswerService answerService) {
		this.answerService = answerService;
	}
	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	//此处使用rest风格的URL
	@RequestMapping("tryAnswer/{qId}")
	public ModelAndView tryAnswer(@PathVariable ("qId") int qId) {
		Question currentQuestion = questionService.getQuestionById(qId);
		ModelAndView mav = new ModelAndView();
		mav.addObject("currentQuestion", currentQuestion);
		mav.addObject("qId", qId);
		mav.setViewName("tryAnswer");
		return mav;
	}
	
	@RequestMapping("addAnswer/{qId}")
	public ModelAndView addAnswer(@PathVariable ("qId") int qId, Answer answer, HttpSession session) {
		ModelAndView mav = new ModelAndView();
		String addAnswerMessage;
		boolean flag;
		User currentUser = (User)session.getAttribute("currentUser");
		if(currentUser == null) {
			addAnswerMessage = "您还未登陆，登陆后才可回答！";
			Question currentQuesstion = questionService.getQuestionById(qId);
			mav.addObject("currentQuestion", currentQuesstion);
			mav.addObject("addAnswerMessage", addAnswerMessage);
			mav.addObject("qId", qId);
			mav.setViewName("tryAnswer");
			return mav;
		} else {
			answer.setaMadeByUserId(currentUser.getuId());
			answer.setaBelongToQuestionId(qId);
			flag = answerService.putAnswer(answer);
			//添加回答成功
			if(flag) {
				Answer currentAnswer = answerService.getAnswerByUser(currentUser.getuId(), qId);
				mav.addObject("aId", currentAnswer.getaId());
				mav.addObject("qId", qId);
				mav.setViewName("redirect:/Question/{qId}/showAnswer/{aId}");
			} else {
				mav.addObject("qId", qId);
				addAnswerMessage = "好像出现了什么问题！";
				Question currentQuesstion = questionService.getQuestionById(qId);
				mav.addObject("currentQuestion", currentQuesstion);
				mav.addObject("addAnswerMessage", addAnswerMessage);
				mav.addObject("qId", qId);
				mav.setViewName("tryAnswer");
			}
		}
		return mav;
	}
	
	@RequestMapping("Question/{qId}/showAnswer/{aId}")
	public ModelAndView showAnswer(@PathVariable("qId") int qId,@PathVariable("aId") int aId) {
		ModelAndView mav = new ModelAndView();
		Question currentQuestion = questionService.getQuestionById(qId);
		Answer currentAnswer = answerService.getAnserById(aId);
		User theAnsweredUser = userService.getUserById(currentAnswer.getaMadeByUserId());
		mav.addObject("currentQuestion", currentQuestion);
		mav.addObject("currentAnswer", currentAnswer);
		mav.addObject("theAnsweredUser", theAnsweredUser);
		mav.setViewName("showAnswer");
		return mav;
	}
}