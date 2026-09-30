(function(){
    var leaveTarget = null;
	
    function modal(id, show){
		var el=document.getElementById(id); 
		if(el){
			el.style.display=show?'flex':'none';
		}
	}
    window.openCommonModal=function(id){
		modal(id,true);
	};
    window.closeCommonModal=function(id){
		modal(id,false);
	};
	
    function isUnsaved(){
		return document.body.getAttribute('data-unsaved')==='true';
	}
    function navigate(url){
		leaveTarget=null; 
		document.body.setAttribute('data-unsaved','false'); 
		window.location.href=url;
	}
	
    document.addEventListener('DOMContentLoaded',function(){
    	var links=document.querySelectorAll('[data-nav-target]');
        
		links.forEach(function(link){
			link.addEventListener('click',function(e){
				if(!isUnsaved()){
					return;
				}
				e.preventDefault();
				leaveTarget=link.getAttribute('data-nav-target');
				openCommonModal('leaveConfirmModal');
		});
	 });
	 
        var cancel=document.getElementById('leaveCancelBtn');
		 
		if(cancel){
		   cancel.onclick=function(){
			  leaveTarget=null;
			  closeCommonModal('leaveConfirmModal');
			};
		}
        
		var confirm=document.getElementById('leaveConfirmBtn');
		
		if(confirm){
		   confirm.onclick=function(){
				if(leaveTarget){
					navigate(leaveTarget);
				}
			};
		}
		
      //  window.addEventListener('beforeunload',function(e){
	  // if(!isUnsaved())return;
	  // e.preventDefault();
	  // e.returnValue='';});
	  
        if(isUnsaved() && window.history && window.history.pushState){
        	history.pushState(
				{guarded:true},'',location.href);
            var allowPop=false;
			window.addEventListener('popstate',function(){
				
                if(allowPop){
					allowPop=false;
					return;
				}
				
                history.pushState({
					guarded:true}, '', location.href
				);
				leaveTarget=null;
                
				openCommonModal('leaveConfirmModal');
                
				var btn=document.getElementById('leaveConfirmBtn');
				
                if(btn){
					btn.onclick=function(){
						allowPop=true;
						document.body.setAttribute('data-unsaved','false');
						history.back();
					};
				}
            });
        }
    });
})();
