
      <tr class="table_foot1 pageBar">
			<td colspan="11" style="text-align:right"><div
					class="result_foot">
					<span>当前&nbsp;${pageNo}&nbsp;/&nbsp;${totalPages
						}&nbsp;页</span>
					<c:if test="${hasPre}">
						<span><a pageStart="0" 
							href="${url}">首页</a>
						</span>
						<span><a pageStart="${preFirst}" 
							href="${url}">上一页</a>
						</span>
					</c:if>
					<c:if test="${hasNext}">
						<span><a pageStart="${nextFirst}" 
							href="${url}">下一页</a>
						</span>
						<span><a pageStart="${lastFirst }" 
							href="${url}"">末页</a>
						</span>
					</c:if>
					<span>跳至<input type="text" class="page_index"
						name="page.pageNo" id="pageNo" width="20px" />页</span>
				</div></td>
		</tr>

<script type="text/javascript">
  $(function(){
    	//绑定分页事件
    	$(".pageBar a").click(function(){
    		var url = $(this).attr("href");
    		var start = $(this).attr("pageStart");
    		var len = $("#${formId}").length;
    		if(len>0){
    		  $("#${formId}").attr("action",url);
    		   var hidden1 = $("<input type='hidden' name='page.start' />").val(start);
    		    $("#${formId}").append(hidden1);
    		   var hidden2 = $("<input type='hidden' name='page.pageSize' />").val("${pageSize}");
    		    $("#${formId}").append(hidden2);
    		  $("#${formId}").submit();
    		
    		  return false;
    		}else{
    			return true;
    		}
    		
    	});
    	
    	// 绑定键盘按下事件  
		   $(document).keypress(function(e) {  
		    // 回车键事件  
		       if(e.which == 13) {  
		    	  var pageNo = $("#pageNo").val();
		    	  var url = "${url}";
		    	   $("#${formId}").attr("action",url);
		    	   var hidden = $("<input type='hidden' name='pageNo' />").val(pageNo);
		    	   $("#${formId}").append(hidden);
		    	  var hidden2 = $("<input type='hidden' name='page.pageSize' />").val("${pageSize}");
    		       $("#${formId}").append(hidden2);
		    	   
		    		$("#${formId}").submit();
		         }  
		   }); 
    	
    });
</script>
