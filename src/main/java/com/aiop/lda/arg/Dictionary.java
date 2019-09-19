/*
 * Copyright (C) 2007 by
 * 
 * 	Xuan-Hieu Phan
 *	hieuxuan@ecei.tohoku.ac.jp or pxhieu@gmail.com
 * 	Graduate School of Information Sciences
 * 	Tohoku University
 * 
 *  Cam-Tu Nguyen
 *  ncamtu@gmail.com
 *  College of Technology
 *  Vietnam National University, Hanoi
 *
 * JGibbsLDA is a free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
 *
 * JGibbsLDA is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JGibbsLDA; if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */
package com.aiop.lda.arg;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.aiop.lda.service.LdaService;

public class Dictionary implements Serializable{
	
	private static final long serialVersionUID = -7709563464351645654L;
	
	public Map<String,Integer> word2id;
	public Map<Integer, String> id2word;
		
	//--------------------------------------------------
	// constructors
	//--------------------------------------------------
	
	public Dictionary(){
		word2id = new HashMap<String, Integer>();
		id2word = new HashMap<Integer, String>();
	}
	
	//---------------------------------------------------
	// get/set methods
	//---------------------------------------------------
	
	public String getWord(int id){
		return id2word.get(id);
	}
	
	public Integer getID (String word){
		return word2id.get(word);
	}
	
	//----------------------------------------------------
	// checking methods
	//----------------------------------------------------
	/**
	 * check if this dictionary contains a specified word
	 */
	public boolean contains(String word){
		return word2id.containsKey(word);
	}
	
	public boolean contains(int id){
		return id2word.containsKey(id);
	}
	//---------------------------------------------------
	// manupulating methods
	//---------------------------------------------------
	/**
	 * add a word into this dictionary
	 * return the corresponding id
	 */
	public int addWord(String word){
		if (!contains(word)){
			int id = word2id.size();
			
			word2id.put(word, id);
			id2word.put(id,word);
			
			return id;
		}
		else return getID(word);		
	}
	
	//---------------------------------------------------
	// I/O methods
	//---------------------------------------------------
	/**
	 * read dictionary from file
	 */
	public boolean readWordMap(){		
		try{
			List<Map<String, Object>> datas=LdaService.queryData(LdaService.WORDMAP_INDEX_NAME);
			
			//read the number of words
			int nwords = datas.size();
			
			//read map
			for (int i = 0; i < nwords; ++i){
				String word = (String) datas.get(i).get("word");
				int intID = (int) datas.get(i).get("wordId");
				
				id2word.put(intID, word);
				word2id.put(word, intID);
			}
			
			return true;
		}
		catch (Exception e){
			System.out.println("Error while reading dictionary:" + e.getMessage());
			e.printStackTrace();
			return false;
		}		
	}
	
	public boolean writeWordMap(){
		try{
			List<HashMap<String,Object>> datas=new ArrayList<HashMap<String,Object>>();
			
			//write word to id
			Iterator<String> it = word2id.keySet().iterator();
			while (it.hasNext()){
				String key = it.next();
				Integer value = word2id.get(key);
				HashMap<String,Object> tmp=new HashMap<String, Object>();
				tmp.put("word", key);
				tmp.put("wordId", value);
				datas.add(tmp);
				if(datas.size()%200==0){
					LdaService.insertBatchLineForMap(LdaService.WORDMAP_INDEX_NAME,datas);
					datas.clear();
				}
			}
			
			if(datas.size()>0){
				LdaService.insertBatchLineForMap(LdaService.WORDMAP_INDEX_NAME, datas);
			}
			return true;
		}
		catch (Exception e){
			System.out.println("Error while writing word map " + e.getMessage());
			e.printStackTrace();
			return false;
		}
		
		
	}
}
