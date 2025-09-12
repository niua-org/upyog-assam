insert into state.egdcr_layername(id,key,value,createdby,createddate,lastmodifiedby,lastmodifieddate,version) 
select nextval('state.seq_egdcr_layername'),'LAYER_NAME_LAUNDRY_RECREATION_VENTILATION','BLK_%s_FLR_%s_LAUNDRY_RECREATION_VENTILATION_%s',1,now(),1,now(),0 where not exists(select key from state.egdcr_layername where key='LAYER_NAME_LAUNDRY_RECREATION_VENTILATION');

insert into state.egdcr_layername(id,key,value,createdby,createddate,lastmodifiedby,lastmodifieddate,version) 
select nextval('state.seq_egdcr_layername'),'LAYER_NAME_KITCHEN_DINING_VENTILATION','BLK_%s_FLR_%s_KITCHEN_DINING_VENTILATION',1,now(),1,now(),0 where not exists(select key from state.egdcr_layername where key='LAYER_NAME_KITCHEN_DINING_VENTILATION');

insert into state.egdcr_layername(id,key,value,createdby,createddate,lastmodifiedby,lastmodifieddate,version) 
select nextval('state.seq_egdcr_layername'),'LAYER_NAME_FURTHEST_COR_OF_BUILDING','BLK_%s_FURTHEST_COR_OF_BUILDING',1,now(),1,now(),0 where not exists(select key from state.egdcr_layername where key='LAYER_NAME_FURTHEST_COR_OF_BUILDING');

insert into state.egdcr_layername(id,key,value,createdby,createddate,lastmodifiedby,lastmodifieddate,version) 
select nextval('state.seq_egdcr_layername'),'LAYER_NAME_NON_INHABITABLE_ROOM','BLK_%s_FLR_%s_NON_INHABITABLE_ROOM_%s',1,now(),1,now(),0 where not exists(select key from state.egdcr_layername where key='LAYER_NAME_NON_INHABITABLE_ROOM');

insert into state.egdcr_layername(id,key,value,createdby,createddate,lastmodifiedby,lastmodifieddate,version) 
select nextval('state.seq_egdcr_layername'),'LAYER_NAME_HILLY_ROOM_HEIGHT','BLK_%s_FLR_%s_HILLY_ROOM_HEIGHT_%s',1,now(),1,now(),0 where not exists(select key from state.egdcr_layername where key='LAYER_NAME_HILLY_ROOM_HEIGHT');

insert into state.egdcr_layername(id,key,value,createdby,createddate,lastmodifiedby,lastmodifieddate,version) 
select nextval('state.seq_egdcr_layername'),'LAYER_NAME_MEZZANINE_AT_NON_INHABITABLE_ROOM','BLK_%s_FLR_%s_ROOM_%s_NONINHABITABLE_ROOM_MEZ_AREA_%s',1,now(),1,now(),0 where not exists(select key from state.egdcr_layername where key='LAYER_NAME_MEZZANINE_AT_NON_INHABITABLE_ROOM');


