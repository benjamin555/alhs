
      <tr class="table_foot1 pageBar">
			<td colspan="11" style="text-align:right"><div
					class="result_foot">
					<span>��ǰ&nbsp;${pageNo}&nbsp;/&nbsp;${totalPages
						}&nbsp;ҳ</span>
					<c:if test="${hasPre}">
						<span><a pageStart="0" 
							href="${url}">��ҳ</a>
						</span>
						<span><a pageStart="${preFirst}" 
							href="${url}">��һҳ</a>
						</span>
					</c:if>
					<c:if test="${hasNext}">
						<span><a pageStart="${nextFirst}" 
							href="${url}">��һҳ</a>
						</span>
						<span><a pageStart="${lastFirst }" 
							href="${url}"">ĩҳ</a>
						</span>
					</c:if>
					<span>����<input type="text" class="page_index"
						name="page.pageNo" id="pageNo" width="20px" />ҳ</span>
				</div></td>
		</tr>

<script type="text/javascript">
  $(function(){
    	//�󶨷�ҳ�¼�
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
    	
    	// �󶨼��̰����¼�  
		   $(document).keypress(function(e) {  
		    // �س����¼�  
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
