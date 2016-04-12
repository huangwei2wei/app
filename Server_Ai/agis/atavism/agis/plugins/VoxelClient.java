// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.plugins;

import atavism.server.messages.PropertyMessage;
import atavism.server.util.Log;
import atavism.msgsys.Message;
import atavism.server.engine.Engine;
import atavism.agis.objects.BuildObjectTemplate;
import atavism.msgsys.MessageType;

public class VoxelClient
{
    public static final MessageType MSG_TYPE_CREATE_CLAIM;
    public static final MessageType MSG_TYPE_EDIT_CLAIM;
    public static final MessageType MSG_TYPE_PURCHASE_CLAIM;
    public static final MessageType MSG_TYPE_SELL_CLAIM;
    public static final MessageType MSG_TYPE_DELETE_CLAIM;
    public static final MessageType MSG_TYPE_CLAIM_PERMISSION;
    public static final MessageType MSG_TYPE_CLAIM_ACTION;
    public static final MessageType MSG_TYPE_PLACE_CLAIM_OBJECT;
    public static final MessageType MSG_TYPE_EDIT_CLAIM_OBJECT;
    public static final MessageType MSG_TYPE_GET_RESOURCES;
    public static final MessageType MSG_TYPE_NO_BUILD_CLAIM_TRIGGER;
    public static final MessageType MSG_TYPE_UPGRADE_BUILDING_OBJECT;
    public static final MessageType MSG_TYPE_GET_BUILDING_TEMPLATE;
    public static final MessageType MSG_TYPE_GET_CLAIM_OBJECT_INFO;
    public static final MessageType MSG_TYPE_ATTACK_BUILDING_OBJECT;
    
    static {
        MSG_TYPE_CREATE_CLAIM = MessageType.intern("voxel.CREATE_CLAIM");
        MSG_TYPE_EDIT_CLAIM = MessageType.intern("voxel.EDIT_CLAIM");
        MSG_TYPE_PURCHASE_CLAIM = MessageType.intern("voxel.PURCHASE_CLAIM");
        MSG_TYPE_SELL_CLAIM = MessageType.intern("voxel.SELL_CLAIM");
        MSG_TYPE_DELETE_CLAIM = MessageType.intern("voxel.DELETE_CLAIM");
        MSG_TYPE_CLAIM_PERMISSION = MessageType.intern("voxel.CLAIM_PERMISSION");
        MSG_TYPE_CLAIM_ACTION = MessageType.intern("voxel.CLAIM_ACTION");
        MSG_TYPE_PLACE_CLAIM_OBJECT = MessageType.intern("voxel.PLACE_CLAIM_OBJECT");
        MSG_TYPE_EDIT_CLAIM_OBJECT = MessageType.intern("voxel.EDIT_CLAIM_OBJECT");
        MSG_TYPE_GET_RESOURCES = MessageType.intern("voxel.GET_RESOURCES");
        MSG_TYPE_NO_BUILD_CLAIM_TRIGGER = MessageType.intern("voxel.NO_BUILD_CLAIM_TRIGGER");
        MSG_TYPE_UPGRADE_BUILDING_OBJECT = MessageType.intern("voxel.UPGRADE_BUILDING_OBJECT");
        MSG_TYPE_GET_BUILDING_TEMPLATE = MessageType.intern("voxel.GET_BUILDING_TEMPLATE");
        MSG_TYPE_GET_CLAIM_OBJECT_INFO = MessageType.intern("voxel.GET_CLAIM_OBJECT_INFO");
        MSG_TYPE_ATTACK_BUILDING_OBJECT = MessageType.intern("voxel.ATTACK_BUILDING_OBJECT");
    }
    
    public static BuildObjectTemplate getBuildingTemplate(final int templateID) {
        final GetBuildingTemplateMessage msg = new GetBuildingTemplateMessage(templateID);
        final BuildObjectTemplate template = (BuildObjectTemplate)Engine.getAgent().sendRPCReturnObject((Message)msg);
        Log.debug("VOXEL CLIENT: GetBuildingTemplateMessage hit");
        return template;
    }
    
    public static class GetBuildingTemplateMessage extends PropertyMessage
    {
        private static final long serialVersionUID = 1L;
        int templateID;
        
        public GetBuildingTemplateMessage() {
        }
        
        public GetBuildingTemplateMessage(final int templateID) {
            this.setMsgType(VoxelClient.MSG_TYPE_GET_BUILDING_TEMPLATE);
            this.setTemplateID(templateID);
        }
        
        public int getTemplateID() {
            return this.templateID;
        }
        
        public void setTemplateID(final int templateID) {
            this.templateID = templateID;
        }
    }
}
