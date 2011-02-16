<?xml version="1.0" encoding="UTF-8"?> 

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

#beginManagementTool("tool.managementtool.viewContentTypeDefinition.header" "tool.managementtool.viewContentTypeDefinition.header" "contentTypeDefinitionId=$contentTypeDefinitionId&name=$name")

<div class="fullymarginalized">

<table class="managementtooledit" cellpadding="2" cellspacing="2" border="0" width="100%" height="30">
<form name="editForm" method="POST" action="UpdateContentTypeDefinition.action">
	<tr>
		<td>
			<p>#editTextField("ContentTypeDefinition.name" "name" $!name "51" "normaltextfield" "")</p>
			<p>#editTextArea("ContentTypeDefinition.schemaValue" "schemaValue" $!schemaValue "60" "25" "largetextarea")</p>
		</td>
	</tr>
	<tr>
		<td>&nbsp;</th>
	</tr>
	<tr>
		<td>
			<input type="image" src="$ui.getString("images.managementtool.buttons.save")" width="50" height="25"> 
			<a href="javascript:saveAndExit(document.editForm, 'UpdateContentTypeDefinition!saveAndExit.action');"><img src="$ui.getString("images.managementtool.buttons.saveAndExit")" width="80" height="25" border="0"></a>
			<a href="ViewListContentTypeDefinition.action"><img src="$ui.getString("images.managementtool.buttons.cancel")" width="50" height="25" border="0"></a>
		</td>
	</tr>

<input type="hidden" name="contentTypeDefinitionId" value="$!contentTypeDefinitionId">
</form>
</table>
</div>

</body>
</html>


