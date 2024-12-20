package com.dmm.task.controller;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.dmm.task.data.entity.Tasks;
import com.dmm.task.data.repository.TasksRepository;
import com.dmm.task.form.TaskForm;
import com.dmm.task.service.AccountUserDetails;

@Controller
public class MainController {

	@Autowired
	private TasksRepository repo;
	

   

	/**
	 * タスクの一覧表示.
	 * 
	 * @param model モデル
	 * @return 遷移先
	 */
	@GetMapping("/main")
	public String main(Model model,@AuthenticationPrincipal AccountUserDetails user,@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
		
		//権限一覧の取得
		Set<String> roles = AuthorityUtils.authorityListToSet(user.getAuthorities());
		
		//dateが空ならば今日の日付を入れる
		if(date == null) {
		date = LocalDate.now();
		}
		
		//前の月のカレンダーを表示
		model.addAttribute("prev",date.minusMonths(1));
		//次の月のカレンダーを表示
		model.addAttribute("next",date.plusMonths(1));
		
		// 週と日を格納する二次元配列を用意する
	    List<List<LocalDate>> month = new ArrayList<>();
	    
	    
	    //1週間分のLocalDateを格納するListを用意する
	    List<LocalDate> week = new ArrayList<>();
	    
	    //その月の1日のLocalDateを取得する
	    LocalDate This1stDay = date.withDayOfMonth(1);
	    LocalDate previousDate =This1stDay;
	    LocalDate currentDate =This1stDay;
	    
	    //曜日を表すDayOfWeekを取得
	    int dayValue = This1stDay.getDayOfWeek().getValue();
	    
	    //もし、1日が日曜日で無ければ以下の処理を行う
	   
	    //上で取得したLocalDateに曜日の値（DayOfWeek#getValue)をマイナスして前月分のLocalDateを求める
	    if(dayValue != 7) {
		    previousDate = This1stDay.minusDays(dayValue);
	    }
		    //1日ずつ増やしてLocalDateを求めていき、List:weekへ格納していき、1週間分詰めたら1．のリストへ格納する
			currentDate =  previousDate;
			
		    for(int i = 0; i < 7; i++) {
				
				currentDate =previousDate.plusDays(i);
				week.add(currentDate);
			}
		   
		    month.add(week);
		    week = new ArrayList<>();
	    
	  	
	    
	    // 2週目以降は単純に1日ずつ日を増やしながらLocalDateを求めてListへ格納
	    int originDate=currentDate.getDayOfMonth(); 
	    int lastDate=currentDate.lengthOfMonth();
	    	
	    for(int i = originDate; i<lastDate;i++) {
	    	
	    	currentDate= currentDate.plusDays(1);
	    	week.add(currentDate);
	    	
	    	//土曜日になったら1．のリストへ格納して新しいListを生成する
	    	if(currentDate.getDayOfWeek()== DayOfWeek.SATURDAY) {
	    		 month.add(week);
	    		 week = new ArrayList<>(); 
	    	}
	    }
		
	    //現在の曜日を求める
	    int dayOflastDate = currentDate.getDayOfWeek().getValue();
	    
	    //日曜日だと土曜日のvalueより大きくなるため、7を引く
	    if(dayOflastDate==7) {
	    	dayOflastDate = dayOflastDate - 7;
	    }
	    
	  //月の最終日が土曜日でなければ
	    if( currentDate.getDayOfWeek()!= DayOfWeek.SATURDAY) {
		    for(int i = dayOflastDate ; i <  DayOfWeek.SATURDAY.getValue(); i++) {
		    	currentDate= currentDate.plusDays(1);
		    	week.add(currentDate);
		    }
	    }
	    month.add(week);
	    
	    
	    LinkedMultiValueMap<LocalDate, Tasks> tasks= new LinkedMultiValueMap<LocalDate, Tasks>();
	    
	    
	    // リポジトリ経由でタスクを取得
	    List<Tasks> list;
	    	    
	    if(roles.contains("ADMIN")) {
	    	//ADMIN権限のユーザは全ユーザのタスクを取得
	    	list= repo.findAll(Sort.by(Sort.Direction.DESC, "id"));
	    }else {
	    	//それ以外のユーザはログイン中のユーザのみのタスクを取得
	    	list= repo.findByDateBetween(previousDate.atTime(0, 0),currentDate.atTime(0, 0),user.getName());
	    }

	    // 取得したタスクをコレクションに追加
	    for (Tasks task : list) {
	        tasks.add(task.getDate().toLocalDate(), task);
	    }

	    // コレクションのデータをHTMLに連携	    
	    model.addAttribute("matrix",month);	   
	    model.addAttribute("tasks",tasks);
		
	
		//今の月を表示する
		DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy年M月");
		String CurrentMonth = f.format(This1stDay);
		model.addAttribute("month", CurrentMonth);
		
		return "main";
	}
	
	
	// タスク登録画面の表示用
	@GetMapping("/main/create/{date}")
	public String create(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,Model model) {
	
	  return "create";
	}

	// タスク登録用
	@PostMapping("/main/create")
	public String createPost(TaskForm taskForm,@AuthenticationPrincipal AccountUserDetails user, Model model) {
		Tasks task = new Tasks();
		
		//エンティティにフォームからのデータを渡す
		task.setName(user.getName());
		task.setTitle(taskForm.getTitle());
		task.setText(taskForm.getText());
//		task.setDate(taskForm.getDate().atTime(0, 0));
		task.setDate(LocalDateTime.of(taskForm.getDate(),taskForm.getTime()));
		task.setDone(false);
		
		//DBへ登録
		repo.save(task);
	
		return "redirect:/main";

	}
	
	// タスク編集画面の表示用
	@GetMapping("/main/edit/{id}")
	public String edit(@PathVariable Integer id,Model model ){
		 Tasks task = repo.getById(id);
		 model.addAttribute("task", task);
		 return "edit";
		
	}
	
	//タスク編集更新用
	@PostMapping("/main/edit/{id}")
	public String update(TaskForm taskForm,@PathVariable Integer id,@AuthenticationPrincipal AccountUserDetails user, Model model){
			 
		Tasks task = new Tasks();
		
		//エンティティにフォームからのデータを渡す
		task.setName(user.getName());
		task.setTitle(taskForm.getTitle());
		task.setText(taskForm.getText());
		//2024.11.22 時刻も入力可能にする Start
		//task.setDate(taskForm.getDate().atTime(0, 0));
		task.setDate(LocalDateTime.of(taskForm.getDate(),taskForm.getTime()));
		//2024.11.22 時刻も入力可能にする End
		task.setDone(taskForm.isDone());
		
		//DBへ更新
		repo.deleteById(id);
		repo.save(task);
		return "redirect:/main";			
		}
	
	//タスク削除用
	@PostMapping("/main/delete/{id}")
	public String delete(@PathVariable Integer id) {
		repo.deleteById(id);
		return "redirect:/main";
	}

}