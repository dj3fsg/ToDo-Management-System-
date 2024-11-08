package com.dmm.task.controller;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;



@Controller
public class MainController {

	

	/**
	 * 投稿の一覧表示.
	 * 
	 * @param model モデル
	 * @return 遷移先
	 */
	@GetMapping("/main")
	public String main() {
		//1. 週と日を格納する二次元配列を用意する
	    List<List<LocalDate>> month = new ArrayList<>();
	    
	    //2.1週間分のLocalDateを格納するListを用意する
	    List<LocalDate> week = new ArrayList<>();
	    
	    //3.その月の1日のLocalDateを取得する
	    LocalDate This1stDay = LocalDate.now().withDayOfMonth(1);
	    
	    //4-1.曜日を表すDayOfWeekを取得
	    int dayValue = This1stDay.getDayOfWeek().getValue();
	    
	    //4-2.上で取得したLocalDateに曜日の値（DayOfWeek#getValue)をマイナスして前月分のLocalDateを求める
	    LocalDate previousDate = This1stDay.minusDays(dayValue);
	    
	    //5.1日ずつ増やしてLocalDateを求めていき、2．で作成したListへ格納していき、1週間分詰めたら1．のリストへ格納する
		LocalDate currentDate =  previousDate;
	    for(int i = 0; i < 7; i++) {
			
			currentDate = previousDate.plusDays(i);
			week.add(currentDate);
		}
		
		month.add(week);
		
		
		
		
	    //6. 2週目以降は単純に1日ずつ日を増やしながらLocalDateを求めてListへ格納
	    while(currentDate.getDayOfMonth()<currentDate.lengthOfMonth()) {
	    	currentDate= currentDate.plusDays(1);
	    	week.add(currentDate);
	    	
	    	//土曜日になったら1．のリストへ格納して新しいListを生成する
	    	if(currentDate.getDayOfWeek()== DayOfWeek.SATURDAY) {
	    		month.add(week);
	    		
	    	}
	    }
		
	    //現在の曜日を求める
	    int dayOflastDate = currentDate.getDayOfWeek().getValue();
	    
	    for(int i = dayOflastDate ; i <  DayOfWeek.SATURDAY.getValue(); i++) {
	    	currentDate= currentDate.plusDays(1);
	    	week.add(currentDate);
	    }
	    month.add(week);
		
		
		return "/main";
	}
}