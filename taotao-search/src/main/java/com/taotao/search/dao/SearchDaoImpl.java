package com.taotao.search.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.taotao.search.pojo.Item;
import com.taotao.search.pojo.SearchResult;
/**
 * 商品搜索DAO
 * @author a07
 *
 */
@Repository
public class SearchDaoImpl implements SearchDao {

	@Autowired
	private SolrServer solrServer;
	@Override
	public SearchResult search(SolrQuery query)throws Exception {
		//返回值对象
		SearchResult result = new SearchResult();
		//根据查询条件查询索引库
		QueryResponse queryResponse = solrServer.query(query);
		//取查询结果
		SolrDocumentList documentList = queryResponse.getResults();
		//商品列表
		List<Item> itemList = new ArrayList<>();
		//高亮显示		
		Map<String, Map<String, List<String>>> highLighting = queryResponse.getHighlighting();
		for (SolrDocument solrDocument : documentList) {
			Item item = new Item();
			item.setId((String) solrDocument.get("id"));
			List<String> list = highLighting.get(solrDocument.get("id")).get("item_title");
			String title = "";
			if(list != null && list.size()>0){
				title = list.get(0);
			}else{
				title = (String) solrDocument.get("item_title");
			}
			item.setTitle(title);
			item.setImage((String) solrDocument.get("item_image"));
			item.setPrice((long) solrDocument.get("item_price"));
			item.setSell_point((String) solrDocument.get("item_sell_point"));
			item.setCategory_name((String) solrDocument.get("item_category_name"));
			
			itemList.add(item);
		}
		result.setItemList(itemList);
		return result;
	}

}
