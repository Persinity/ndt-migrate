--------------------------------------------------------
--  DDL for Table BLC_ADDITIONAL_OFFER_INFO
--------------------------------------------------------

CREATE TABLE blc_additional_offer_info
  (blc_order_order_id NUMBER(19,0),
  offer_info_id NUMBER(19,0),
  offer_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_ADDRESS
--------------------------------------------------------

CREATE TABLE blc_address
  (address_id NUMBER(19,0),
  address_line1 VARCHAR2(255 CHAR),
  address_line2 VARCHAR2(255 CHAR),
  address_line3 VARCHAR2(255 CHAR),
  city VARCHAR2(255 CHAR),
  company_name VARCHAR2(255 CHAR),
  county VARCHAR2(255 CHAR),
  email_address VARCHAR2(255 CHAR),
  fax VARCHAR2(255 CHAR),
  first_name VARCHAR2(255 CHAR),
  is_active NUMBER(1,0),
  is_business NUMBER(1,0),
  is_default NUMBER(1,0),
  last_name VARCHAR2(255 CHAR),
  postal_code VARCHAR2(255 CHAR),
  primary_phone VARCHAR2(255 CHAR),
  secondary_phone VARCHAR2(255 CHAR),
  standardized NUMBER(1,0),
  tokenized_address VARCHAR2(255 CHAR),
  verification_level VARCHAR2(255 CHAR),
  zip_four VARCHAR2(255 CHAR),
  country VARCHAR2(255 CHAR),
  phone_fax_id NUMBER(19,0),
  phone_primary_id NUMBER(19,0),
  phone_secondary_id NUMBER(19,0),
  state_prov_region VARCHAR2(255 CHAR)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_ADMIN_MODULE
--------------------------------------------------------

CREATE TABLE blc_admin_module
  (admin_module_id NUMBER(19,0),
  display_order NUMBER(10,0),
  icon VARCHAR2(255 CHAR),
  module_key VARCHAR2(255 CHAR),
  name VARCHAR2(255 CHAR)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_ADMIN_PASSWORD_TOKEN
--------------------------------------------------------

CREATE TABLE blc_admin_password_token
  (password_token VARCHAR2(255 CHAR),
  admin_user_id NUMBER(19,0),
  create_date TIMESTAMP (6),
  token_used_date TIMESTAMP (6),
  token_used_flag NUMBER(1,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_ADMIN_PERMISSION
--------------------------------------------------------

CREATE TABLE blc_admin_permission
  (admin_permission_id NUMBER(19,0),
  description VARCHAR2(255 CHAR),
  name VARCHAR2(255 CHAR),
  permission_type VARCHAR2(255 CHAR)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_ADMIN_PERMISSION_ENTITY
--------------------------------------------------------

CREATE TABLE blc_admin_permission_entity
  (admin_permission_entity_id NUMBER(19,0),
  ceiling_entity VARCHAR2(255 CHAR),
  admin_permission_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_ADMIN_ROLE
--------------------------------------------------------

CREATE TABLE blc_admin_role
  (admin_role_id NUMBER(19,0),
  description VARCHAR2(255 CHAR),
  name VARCHAR2(255 CHAR)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_ADMIN_ROLE_PERMISSION_XREF
--------------------------------------------------------

CREATE TABLE blc_admin_role_permission_xref
  (admin_role_id NUMBER(19,0),
  admin_permission_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_ADMIN_SECTION
--------------------------------------------------------

CREATE TABLE blc_admin_section
  (admin_section_id NUMBER(19,0),
  ceiling_entity VARCHAR2(255 CHAR),
  display_controller VARCHAR2(255 CHAR),
  display_order NUMBER(10,0),
  name VARCHAR2(255 CHAR),
  section_key VARCHAR2(255 CHAR),
  url VARCHAR2(255 CHAR),
  use_default_handler NUMBER(1,0),
  admin_module_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_ADMIN_SEC_PERM_XREF
--------------------------------------------------------

CREATE TABLE blc_admin_sec_perm_xref
  (admin_section_id NUMBER(19,0),
  admin_permission_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_ADMIN_USER
--------------------------------------------------------

CREATE TABLE blc_admin_user
  (admin_user_id NUMBER(19,0),
  active_status_flag NUMBER(1,0),
  email VARCHAR2(255 CHAR),
  login VARCHAR2(255 CHAR),
  name VARCHAR2(255 CHAR),
  password VARCHAR2(255 CHAR),
  phone_number VARCHAR2(255 CHAR)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_ADMIN_USER_PERMISSION_XREF
--------------------------------------------------------

CREATE TABLE blc_admin_user_permission_xref
  (admin_user_id NUMBER(19,0),
  admin_permission_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_ADMIN_USER_ROLE_XREF
--------------------------------------------------------

CREATE TABLE blc_admin_user_role_xref
  (admin_user_id NUMBER(19,0),
  admin_role_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_ADMIN_USER_SANDBOX
--------------------------------------------------------

CREATE TABLE blc_admin_user_sandbox
  (sandbox_id NUMBER(19,0),
  admin_user_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_AMOUNT_ITEM
--------------------------------------------------------

CREATE TABLE blc_amount_item
  (amount_item_id NUMBER(19,0),
  description VARCHAR2(255 CHAR),
  quantity NUMBER(19,0),
  short_description VARCHAR2(255 CHAR),
  system_id VARCHAR2(255 CHAR),
  unit_price NUMBER(19,5),
  payment_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_ASSET_DESC_MAP
--------------------------------------------------------

CREATE TABLE blc_asset_desc_map
  (static_asset_id NUMBER(19,0),
  static_asset_desc_id NUMBER(19,0),
  map_key VARCHAR2(255 CHAR)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_BANK_ACCOUNT_PAYMENT
--------------------------------------------------------

CREATE TABLE blc_bank_account_payment
  (payment_id NUMBER(19,0),
  account_number VARCHAR2(255 CHAR),
  reference_number VARCHAR2(255 CHAR),
  routing_number VARCHAR2(255 CHAR)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_BUNDLE_ORDER_ITEM
--------------------------------------------------------

CREATE TABLE blc_bundle_order_item
  (base_retail_price NUMBER(19,5),
  base_sale_price NUMBER(19,5),
  order_item_id NUMBER(19,0),
  product_bundle_id NUMBER(19,0),
  sku_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_BUND_ITEM_FEE_PRICE
--------------------------------------------------------

CREATE TABLE blc_bund_item_fee_price
  (bund_item_fee_price_id NUMBER(19,0),
  amount NUMBER(19,5),
  is_taxable NUMBER(1,0),
  name VARCHAR2(255 CHAR),
  reporting_code VARCHAR2(255 CHAR),
  bund_order_item_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_CANDIDATE_FG_OFFER
--------------------------------------------------------

CREATE TABLE blc_candidate_fg_offer
  (candidate_fg_offer_id NUMBER(19,0),
  discounted_price NUMBER(19,5),
  fulfillment_group_id NUMBER(19,0),
  offer_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_CANDIDATE_ITEM_OFFER
--------------------------------------------------------

CREATE TABLE blc_candidate_item_offer
  (candidate_item_offer_id NUMBER(19,0),
  discounted_price NUMBER(19,5),
  offer_id NUMBER(19,0),
  order_item_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_CANDIDATE_ORDER_OFFER
--------------------------------------------------------

CREATE TABLE blc_candidate_order_offer
  (candidate_order_offer_id NUMBER(19,0),
  discounted_price NUMBER(19,5),
  offer_id NUMBER(19,0),
  order_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_CATALOG
--------------------------------------------------------

CREATE TABLE blc_catalog
  (catalog_id NUMBER(19,0),
  name VARCHAR2(255 CHAR)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_CATEGORY
--------------------------------------------------------

CREATE TABLE blc_category
  (category_id NUMBER(19,0),
  active_end_date TIMESTAMP (6),
  active_start_date TIMESTAMP (6),
  archived CHAR(1 CHAR),
  description VARCHAR2(255 CHAR),
  display_template VARCHAR2(255 CHAR),
  fulfillment_type VARCHAR2(255 CHAR),
  inventory_type VARCHAR2(255 CHAR),
  long_description CLOB,
  name VARCHAR2(255 CHAR),
  tax_code VARCHAR2(255 CHAR),
  url VARCHAR2(255 CHAR),
  url_key VARCHAR2(255 CHAR),
  default_parent_category_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_CATEGORY_ATTRIBUTE
--------------------------------------------------------

CREATE TABLE blc_category_attribute
  (category_attribute_id NUMBER(19,0),
  name VARCHAR2(255 CHAR),
  searchable NUMBER(1,0),
  value VARCHAR2(255 CHAR),
  category_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_CATEGORY_IMAGE
--------------------------------------------------------

CREATE TABLE blc_category_image
  (category_id NUMBER(19,0),
  url VARCHAR2(255 CHAR),
  name VARCHAR2(255 CHAR)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_CATEGORY_MEDIA_MAP
--------------------------------------------------------

CREATE TABLE blc_category_media_map
  (blc_category_category_id NUMBER(19,0),
  media_id NUMBER(19,0),
  map_key VARCHAR2(255 CHAR)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_CATEGORY_PRODUCT_XREF
--------------------------------------------------------

CREATE TABLE blc_category_product_xref
  (display_order NUMBER(19,0),
  category_id NUMBER(19,0),
  product_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_CATEGORY_XREF
--------------------------------------------------------

CREATE TABLE blc_category_xref
  (display_order NUMBER(19,0),
  sub_category_id NUMBER(19,0),
  category_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_CAT_SEARCH_FACET_EXCL_XREF
--------------------------------------------------------

CREATE TABLE blc_cat_search_facet_excl_xref
  (cat_excl_search_facet_id NUMBER(19,0),
  category_id NUMBER(19,0),
  search_facet_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_CAT_SEARCH_FACET_XREF
--------------------------------------------------------

CREATE TABLE blc_cat_search_facet_xref
  (category_search_facet_id NUMBER(19,0),
  sequence NUMBER(19,0),
  category_id NUMBER(19,0),
  search_facet_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_CHALLENGE_QUESTION
--------------------------------------------------------

CREATE TABLE blc_challenge_question
  (question_id NUMBER(19,0),
  question VARCHAR2(255 CHAR)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_CODE_TYPES
--------------------------------------------------------

CREATE TABLE blc_code_types
  (code_id NUMBER(19,0),
  code_type VARCHAR2(255 CHAR),
  code_desc VARCHAR2(255 CHAR),
  code_key VARCHAR2(255 CHAR),
  modifiable CHAR(1 CHAR)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_COUNTRY
--------------------------------------------------------

CREATE TABLE blc_country
  (abbreviation VARCHAR2(255 CHAR),
  name VARCHAR2(255 CHAR)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_CREDIT_CARD_PAYMENT
--------------------------------------------------------

CREATE TABLE blc_credit_card_payment
  (payment_id NUMBER(19,0),
  expiration_month NUMBER(10,0),
  expiration_year NUMBER(10,0),
  name_on_card VARCHAR2(255 CHAR),
  pan VARCHAR2(255 CHAR),
  reference_number VARCHAR2(255 CHAR)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_CURRENCY
--------------------------------------------------------

CREATE TABLE blc_currency
  (currency_code VARCHAR2(255 CHAR),
  default_flag NUMBER(1,0),
  friendly_name VARCHAR2(255 CHAR)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_CUSTOMER
--------------------------------------------------------

CREATE TABLE blc_customer
  (customer_id NUMBER(19,0),
  created_by NUMBER(19,0),
  date_created TIMESTAMP (6),
  date_updated TIMESTAMP (6),
  updated_by NUMBER(19,0),
  challenge_answer VARCHAR2(255 CHAR),
  deactivated NUMBER(1,0),
  email_address VARCHAR2(255 CHAR),
  first_name VARCHAR2(255 CHAR),
  last_name VARCHAR2(255 CHAR),
  password VARCHAR2(255 CHAR),
  password_change_required NUMBER(1,0),
  receive_email NUMBER(1,0),
  is_registered NUMBER(1,0),
  tax_exemption_code VARCHAR2(255 CHAR),
  user_name VARCHAR2(255 CHAR),
  challenge_question_id NUMBER(19,0),
  locale_code VARCHAR2(255 CHAR)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_CUSTOMER_ADDRESS
--------------------------------------------------------

CREATE TABLE blc_customer_address
  (customer_address_id NUMBER(19,0),
  address_name VARCHAR2(255 CHAR),
  address_id NUMBER(19,0),
  customer_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_CUSTOMER_ATTRIBUTE
--------------------------------------------------------

CREATE TABLE blc_customer_attribute
  (customer_attr_id NUMBER(19,0),
  name VARCHAR2(255 CHAR),
  value VARCHAR2(255 CHAR),
  customer_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_CUSTOMER_OFFER_XREF
--------------------------------------------------------

CREATE TABLE blc_customer_offer_xref
  (customer_offer_id NUMBER(19,0),
  customer_id NUMBER(19,0),
  offer_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_CUSTOMER_PASSWORD_TOKEN
--------------------------------------------------------

CREATE TABLE blc_customer_password_token
  (password_token VARCHAR2(255 CHAR),
  create_date TIMESTAMP (6),
  customer_id NUMBER(19,0),
  token_used_date TIMESTAMP (6),
  token_used_flag NUMBER(1,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_CUSTOMER_PAYMENT
--------------------------------------------------------

CREATE TABLE blc_customer_payment
  (customer_payment_id NUMBER(19,0),
  is_default NUMBER(1,0),
  payment_token VARCHAR2(255 CHAR),
  address_id NUMBER(19,0),
  customer_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_CUSTOMER_PAYMENT_FIELDS
--------------------------------------------------------

CREATE TABLE blc_customer_payment_fields
  (customer_payment_id NUMBER(19,0),
  field_value VARCHAR2(255 CHAR),
  field_name VARCHAR2(255 CHAR)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_CUSTOMER_PHONE
--------------------------------------------------------

CREATE TABLE blc_customer_phone
  (customer_phone_id NUMBER(19,0),
  phone_name VARCHAR2(255 CHAR),
  customer_id NUMBER(19,0),
  phone_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_CUSTOMER_ROLE
--------------------------------------------------------

CREATE TABLE blc_customer_role
  (customer_role_id NUMBER(19,0),
  customer_id NUMBER(19,0),
  role_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_DATA_DRVN_ENUM
--------------------------------------------------------

CREATE TABLE blc_data_drvn_enum
  (enum_id NUMBER(19,0),
  enum_key VARCHAR2(255 CHAR),
  modifiable NUMBER(1,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_DATA_DRVN_ENUM_VAL
--------------------------------------------------------

CREATE TABLE blc_data_drvn_enum_val
  (enum_val_id NUMBER(19,0),
  display VARCHAR2(255 CHAR),
  hidden NUMBER(1,0),
  enum_key VARCHAR2(255 CHAR),
  enum_type NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_DISCRETE_ORDER_ITEM
--------------------------------------------------------

CREATE TABLE blc_discrete_order_item
  (base_retail_price NUMBER(19,5),
  base_sale_price NUMBER(19,5),
  order_item_id NUMBER(19,0),
  bundle_order_item_id NUMBER(19,0),
  product_id NUMBER(19,0),
  sku_id NUMBER(19,0),
  sku_bundle_item_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_DISC_ITEM_FEE_PRICE
--------------------------------------------------------

CREATE TABLE blc_disc_item_fee_price
  (disc_item_fee_price_id NUMBER(19,0),
  amount NUMBER(19,5),
  name VARCHAR2(255 CHAR),
  reporting_code VARCHAR2(255 CHAR),
  order_item_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_DYN_DISCRETE_ORDER_ITEM
--------------------------------------------------------

CREATE TABLE blc_dyn_discrete_order_item
  (order_item_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_EMAIL_TRACKING
--------------------------------------------------------

CREATE TABLE blc_email_tracking
  (email_tracking_id NUMBER(19,0),
  date_sent TIMESTAMP (6),
  email_address VARCHAR2(255 CHAR),
  type VARCHAR2(255 CHAR)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_EMAIL_TRACKING_CLICKS
--------------------------------------------------------

CREATE TABLE blc_email_tracking_clicks
  (click_id NUMBER(19,0),
  customer_id VARCHAR2(255 CHAR),
  date_clicked TIMESTAMP (6),
  destination_uri VARCHAR2(255 CHAR),
  query_string VARCHAR2(255 CHAR),
  email_tracking_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_EMAIL_TRACKING_OPENS
--------------------------------------------------------

CREATE TABLE blc_email_tracking_opens
  (open_id NUMBER(19,0),
  date_opened TIMESTAMP (6),
  user_agent VARCHAR2(255 CHAR),
  email_tracking_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_FG_ADJUSTMENT
--------------------------------------------------------

CREATE TABLE blc_fg_adjustment
  (fg_adjustment_id NUMBER(19,0),
  adjustment_reason VARCHAR2(255 CHAR),
  adjustment_value NUMBER(19,5),
  fulfillment_group_id NUMBER(19,0),
  offer_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_FG_FEE_TAX_XREF
--------------------------------------------------------

CREATE TABLE blc_fg_fee_tax_xref
  (fulfillment_group_fee_id NUMBER(19,0),
  tax_detail_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_FG_FG_TAX_XREF
--------------------------------------------------------

CREATE TABLE blc_fg_fg_tax_xref
  (fulfillment_group_id NUMBER(19,0),
  tax_detail_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_FG_ITEM_TAX_XREF
--------------------------------------------------------

CREATE TABLE blc_fg_item_tax_xref
  (fulfillment_group_item_id NUMBER(19,0),
  tax_detail_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_FIELD
--------------------------------------------------------

CREATE TABLE blc_field
  (field_id NUMBER(19,0),
  abbreviation VARCHAR2(255 CHAR),
  entity_type VARCHAR2(255 CHAR),
  facet_field_type VARCHAR2(255 CHAR),
  property_name VARCHAR2(255 CHAR),
  searchable NUMBER(1,0),
  translatable NUMBER(1,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_FIELD_SEARCH_TYPES
--------------------------------------------------------

CREATE TABLE blc_field_search_types
  (field_id NUMBER(19,0),
  searchable_field_type VARCHAR2(255 CHAR)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_FLD_DEF
--------------------------------------------------------

CREATE TABLE blc_fld_def
  (fld_def_id NUMBER(19,0),
  allow_multiples NUMBER(1,0),
  column_width VARCHAR2(255 CHAR),
  fld_order NUMBER(10,0),
  fld_type VARCHAR2(255 CHAR),
  friendly_name VARCHAR2(255 CHAR),
  hidden_flag NUMBER(1,0),
  max_length NUMBER(10,0),
  name VARCHAR2(255 CHAR),
  security_level VARCHAR2(255 CHAR),
  text_area_flag NUMBER(1,0),
  vldtn_error_mssg_key VARCHAR2(255 CHAR),
  vldtn_regex VARCHAR2(255 CHAR),
  fld_enum_id NUMBER(19,0),
  fld_group_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_FLD_ENUM
--------------------------------------------------------

CREATE TABLE blc_fld_enum
  (fld_enum_id NUMBER(19,0),
  name VARCHAR2(255 CHAR)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_FLD_ENUM_ITEM
--------------------------------------------------------

CREATE TABLE blc_fld_enum_item
  (fld_enum_item_id NUMBER(19,0),
  fld_order NUMBER(10,0),
  friendly_name VARCHAR2(255 CHAR),
  name VARCHAR2(255 CHAR),
  fld_enum_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_FLD_GROUP
--------------------------------------------------------

CREATE TABLE blc_fld_group
  (fld_group_id NUMBER(19,0),
  init_collapsed_flag NUMBER(1,0),
  name VARCHAR2(255 CHAR)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_FULFILLMENT_GROUP
--------------------------------------------------------

CREATE TABLE blc_fulfillment_group
  (fulfillment_group_id NUMBER(19,0),
  delivery_instruction VARCHAR2(255 CHAR),
  price NUMBER(19,5),
  shipping_price_taxable NUMBER(1,0),
  merchandise_total NUMBER(19,5),
  method VARCHAR2(255 CHAR),
  is_primary NUMBER(1,0),
  reference_number VARCHAR2(255 CHAR),
  retail_price NUMBER(19,5),
  sale_price NUMBER(19,5),
  fulfillment_group_sequnce NUMBER(10,0),
  service VARCHAR2(255 CHAR),
  status VARCHAR2(255 CHAR),
  total NUMBER(19,5),
  total_fee_tax NUMBER(19,5),
  total_fg_tax NUMBER(19,5),
  total_item_tax NUMBER(19,5),
  total_tax NUMBER(19,5),
  type VARCHAR2(255 CHAR),
  address_id NUMBER(19,0),
  fulfillment_option_id NUMBER(19,0),
  order_id NUMBER(19,0),
  personal_message_id NUMBER(19,0),
  phone_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_FULFILLMENT_GROUP_FEE
--------------------------------------------------------

CREATE TABLE blc_fulfillment_group_fee
  (fulfillment_group_fee_id NUMBER(19,0),
  amount NUMBER(19,5),
  fee_taxable_flag NUMBER(1,0),
  name VARCHAR2(255 CHAR),
  reporting_code VARCHAR2(255 CHAR),
  total_fee_tax NUMBER(19,5),
  fulfillment_group_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_FULFILLMENT_GROUP_ITEM
--------------------------------------------------------

CREATE TABLE blc_fulfillment_group_item
  (fulfillment_group_item_id NUMBER(19,0),
  prorated_order_adj NUMBER(19,2),
  quantity NUMBER(10,0),
  status VARCHAR2(255 CHAR),
  total_item_amount NUMBER(19,5),
  total_item_taxable_amount NUMBER(19,5),
  total_item_tax NUMBER(19,5),
  fulfillment_group_id NUMBER(19,0),
  order_item_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_FULFILLMENT_OPTION
--------------------------------------------------------

CREATE TABLE blc_fulfillment_option
  (fulfillment_option_id NUMBER(19,0),
  fulfillment_type VARCHAR2(255 CHAR),
  long_description CLOB,
  name VARCHAR2(255 CHAR),
  tax_code VARCHAR2(255 CHAR),
  taxable NUMBER(1,0),
  use_flat_rates NUMBER(1,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_FULFILLMENT_OPTION_FIXED
--------------------------------------------------------

CREATE TABLE blc_fulfillment_option_fixed
  (price NUMBER(19,5),
  fulfillment_option_id NUMBER(19,0),
  currency_code VARCHAR2(255 CHAR)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_FULFILLMENT_OPT_BANDED_PRC
--------------------------------------------------------

CREATE TABLE blc_fulfillment_opt_banded_prc
  (fulfillment_option_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_FULFILLMENT_OPT_BANDED_WGT
--------------------------------------------------------

CREATE TABLE blc_fulfillment_opt_banded_wgt
  (fulfillment_option_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_FULFILLMENT_PRICE_BAND
--------------------------------------------------------

CREATE TABLE blc_fulfillment_price_band
  (fulfillment_price_band_id NUMBER(19,0),
  result_amount NUMBER(19,5),
  result_amount_type VARCHAR2(255 CHAR),
  retail_price_minimum_amount NUMBER(19,5),
  fulfillment_option_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_FULFILLMENT_WEIGHT_BAND
--------------------------------------------------------

CREATE TABLE blc_fulfillment_weight_band
  (fulfillment_weight_band_id NUMBER(19,0),
  result_amount NUMBER(19,5),
  result_amount_type VARCHAR2(255 CHAR),
  minimum_weight NUMBER(19,5),
  weight_unit_of_measure VARCHAR2(255 CHAR),
  fulfillment_option_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_GIFTWRAP_ORDER_ITEM
--------------------------------------------------------

CREATE TABLE blc_giftwrap_order_item
  (order_item_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_GIFT_CARD_PAYMENT
--------------------------------------------------------

CREATE TABLE blc_gift_card_payment
  (payment_id NUMBER(19,0),
  pan VARCHAR2(255 CHAR),
  pin VARCHAR2(255 CHAR),
  reference_number VARCHAR2(255 CHAR)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_ID_GENERATION
--------------------------------------------------------

CREATE TABLE blc_id_generation
  (id_type VARCHAR2(255 CHAR),
  batch_size NUMBER(19,0),
  batch_start NUMBER(19,0),
  id_min NUMBER(19,0),
  id_max NUMBER(19,0),
  version NUMBER(10,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_IMG_STATIC_ASSET
--------------------------------------------------------

CREATE TABLE blc_img_static_asset
  (height NUMBER(10,0),
  width NUMBER(10,0),
  static_asset_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_ITEM_OFFER_QUALIFIER
--------------------------------------------------------

CREATE TABLE blc_item_offer_qualifier
  (item_offer_qualifier_id NUMBER(19,0),
  quantity NUMBER(19,0),
  offer_id NUMBER(19,0),
  order_item_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_LOCALE
--------------------------------------------------------

CREATE TABLE blc_locale
  (locale_code VARCHAR2(255 CHAR),
  default_flag NUMBER(1,0),
  friendly_name VARCHAR2(255 CHAR),
  use_in_search_index NUMBER(1,0),
  currency_code VARCHAR2(255 CHAR)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_MEDIA
--------------------------------------------------------

CREATE TABLE blc_media
  (media_id NUMBER(19,0),
  alt_text VARCHAR2(255 CHAR),
  tags VARCHAR2(255 CHAR),
  title VARCHAR2(255 CHAR),
  url VARCHAR2(255 CHAR)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_MODULE_CONFIGURATION
--------------------------------------------------------

CREATE TABLE blc_module_configuration
  (module_config_id NUMBER(19,0),
  active_end_date TIMESTAMP (6),
  active_start_date TIMESTAMP (6),
  archived CHAR(1 CHAR),
  created_by NUMBER(19,0),
  date_created TIMESTAMP (6),
  date_updated TIMESTAMP (6),
  updated_by NUMBER(19,0),
  config_type VARCHAR2(255 CHAR),
  is_default NUMBER(1,0),
  module_name VARCHAR2(255 CHAR),
  module_priority NUMBER(10,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_OFFER
--------------------------------------------------------

CREATE TABLE blc_offer
  (offer_id NUMBER(19,0),
  applies_when_rules CLOB,
  applies_to_rules CLOB,
  apply_offer_to_marked_items NUMBER(1,0),
  apply_to_sale_price NUMBER(1,0),
  archived CHAR(1 CHAR),
  automatically_added NUMBER(1,0),
  combinable_with_other_offers NUMBER(1,0),
  offer_delivery_type VARCHAR2(255 CHAR),
  offer_description VARCHAR2(255 CHAR),
  offer_discount_type VARCHAR2(255 CHAR),
  end_date TIMESTAMP (6),
  marketing_messasge VARCHAR2(255 CHAR),
  max_uses_per_customer NUMBER(19,0),
  max_uses NUMBER(10,0),
  offer_name VARCHAR2(255 CHAR),
  offer_item_qualifier_rule VARCHAR2(255 CHAR),
  offer_item_target_rule VARCHAR2(255 CHAR),
  offer_priority NUMBER(10,0),
  qualifying_item_min_total NUMBER(19,5),
  stackable NUMBER(1,0),
  start_date TIMESTAMP (6),
  target_system VARCHAR2(255 CHAR),
  totalitarian_offer NUMBER(1,0),
  use_new_format NUMBER(1,0),
  offer_type VARCHAR2(255 CHAR),
  uses NUMBER(10,0),
  offer_value NUMBER(19,5)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_OFFER_AUDIT
--------------------------------------------------------

CREATE TABLE blc_offer_audit
  (offer_audit_id NUMBER(19,0),
  customer_id NUMBER(19,0),
  offer_code_id NUMBER(19,0),
  offer_id NUMBER(19,0),
  order_id NUMBER(19,0),
  redeemed_date TIMESTAMP (6)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_OFFER_CODE
--------------------------------------------------------

CREATE TABLE blc_offer_code
  (offer_code_id NUMBER(19,0),
  max_uses NUMBER(10,0),
  offer_code VARCHAR2(255 CHAR),
  end_date TIMESTAMP (6),
  start_date TIMESTAMP (6),
  uses NUMBER(10,0),
  offer_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_OFFER_INFO
--------------------------------------------------------

CREATE TABLE blc_offer_info
  (offer_info_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_OFFER_INFO_FIELDS
--------------------------------------------------------

CREATE TABLE blc_offer_info_fields
  (offer_info_fields_id NUMBER(19,0),
  field_value VARCHAR2(255 CHAR),
  field_name VARCHAR2(255 CHAR)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_OFFER_ITEM_CRITERIA
--------------------------------------------------------

CREATE TABLE blc_offer_item_criteria
  (offer_item_criteria_id NUMBER(19,0),
  order_item_match_rule CLOB,
  quantity NUMBER(10,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_OFFER_RULE
--------------------------------------------------------

CREATE TABLE blc_offer_rule
  (offer_rule_id NUMBER(19,0),
  match_rule CLOB
   )
/
--------------------------------------------------------
--  DDL for Table BLC_OFFER_RULE_MAP
--------------------------------------------------------

CREATE TABLE blc_offer_rule_map
  (blc_offer_offer_id NUMBER(19,0),
  offer_rule_id NUMBER(19,0),
  map_key VARCHAR2(255 CHAR)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_ORDER
--------------------------------------------------------

CREATE TABLE blc_order
  (order_id NUMBER(19,0),
  created_by NUMBER(19,0),
  date_created TIMESTAMP (6),
  date_updated TIMESTAMP (6),
  updated_by NUMBER(19,0),
  email_address VARCHAR2(255 CHAR),
  name VARCHAR2(255 CHAR),
  order_number VARCHAR2(255 CHAR),
  order_status VARCHAR2(255 CHAR),
  order_subtotal NUMBER(19,5),
  submit_date TIMESTAMP (6),
  order_total NUMBER(19,5),
  total_shipping NUMBER(19,5),
  total_tax NUMBER(19,5),
  currency_code VARCHAR2(255 CHAR),
  customer_id NUMBER(19,0),
  locale_code VARCHAR2(255 CHAR)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_ORDER_ADJUSTMENT
--------------------------------------------------------

CREATE TABLE blc_order_adjustment
  (order_adjustment_id NUMBER(19,0),
  adjustment_reason VARCHAR2(255 CHAR),
  adjustment_value NUMBER(19,5),
  offer_id NUMBER(19,0),
  order_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_ORDER_ATTRIBUTE
--------------------------------------------------------

CREATE TABLE blc_order_attribute
  (order_attribute_id NUMBER(19,0),
  name VARCHAR2(255 CHAR),
  value VARCHAR2(255 CHAR),
  order_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_ORDER_ITEM
--------------------------------------------------------

CREATE TABLE blc_order_item
  (order_item_id NUMBER(19,0),
  discounts_allowed NUMBER(1,0),
  item_taxable_flag NUMBER(1,0),
  name VARCHAR2(255 CHAR),
  order_item_type VARCHAR2(255 CHAR),
  price NUMBER(19,5),
  quantity NUMBER(10,0),
  retail_price NUMBER(19,5),
  retail_price_override NUMBER(1,0),
  sale_price NUMBER(19,5),
  sale_price_override NUMBER(1,0),
  total_tax NUMBER(19,2),
  category_id NUMBER(19,0),
  gift_wrap_item_id NUMBER(19,0),
  order_id NUMBER(19,0),
  personal_message_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_ORDER_ITEM_ADD_ATTR
--------------------------------------------------------

CREATE TABLE blc_order_item_add_attr
  (order_item_id NUMBER(19,0),
  value VARCHAR2(255 CHAR),
  name VARCHAR2(255 CHAR)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_ORDER_ITEM_ADJUSTMENT
--------------------------------------------------------

CREATE TABLE blc_order_item_adjustment
  (order_item_adjustment_id NUMBER(19,0),
  applied_to_sale_price NUMBER(1,0),
  adjustment_reason VARCHAR2(255 CHAR),
  adjustment_value NUMBER(19,5),
  offer_id NUMBER(19,0),
  order_item_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_ORDER_ITEM_ATTRIBUTE
--------------------------------------------------------

CREATE TABLE blc_order_item_attribute
  (order_item_attribute_id NUMBER(19,0),
  name VARCHAR2(255 CHAR),
  value VARCHAR2(255 CHAR),
  order_item_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_ORDER_ITEM_DTL_ADJ
--------------------------------------------------------

CREATE TABLE blc_order_item_dtl_adj
  (order_item_dtl_adj_id NUMBER(19,0),
  applied_to_sale_price NUMBER(1,0),
  offer_name VARCHAR2(255 CHAR),
  adjustment_reason VARCHAR2(255 CHAR),
  adjustment_value NUMBER(19,5),
  offer_id NUMBER(19,0),
  order_item_price_dtl_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_ORDER_ITEM_PRICE_DTL
--------------------------------------------------------

CREATE TABLE blc_order_item_price_dtl
  (order_item_price_dtl_id NUMBER(19,0),
  quantity NUMBER(10,0),
  use_sale_price NUMBER(1,0),
  order_item_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_ORDER_MULTISHIP_OPTION
--------------------------------------------------------

CREATE TABLE blc_order_multiship_option
  (order_multiship_option_id NUMBER(19,0),
  address_id NUMBER(19,0),
  fulfillment_option_id NUMBER(19,0),
  order_id NUMBER(19,0),
  order_item_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_ORDER_OFFER_CODE_XREF
--------------------------------------------------------

CREATE TABLE blc_order_offer_code_xref
  (order_id NUMBER(19,0),
  offer_code_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_ORDER_PAYMENT
--------------------------------------------------------

CREATE TABLE blc_order_payment
  (payment_id NUMBER(19,0),
  amount NUMBER(19,5),
  customer_ip_address VARCHAR2(255 CHAR),
  reference_number VARCHAR2(255 CHAR),
  payment_type VARCHAR2(255 CHAR),
  address_id NUMBER(19,0),
  customer_payment_id NUMBER(19,0),
  order_id NUMBER(19,0),
  phone_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_ORDER_PAYMENT_DETAILS
--------------------------------------------------------

CREATE TABLE blc_order_payment_details
  (payment_detail_id NUMBER(19,0),
  payment_amount NUMBER(19,2),
  date_recorded TIMESTAMP (6),
  payment_info_detail_type VARCHAR2(255 CHAR),
  currency_code VARCHAR2(255 CHAR),
  payment_info NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_PAGE
--------------------------------------------------------

CREATE TABLE blc_page
  (page_id NUMBER(19,0),
  archived_flag NUMBER(1,0),
  created_by NUMBER(19,0),
  date_created TIMESTAMP (6),
  date_updated TIMESTAMP (6),
  updated_by NUMBER(19,0),
  deleted_flag NUMBER(1,0),
  description VARCHAR2(255 CHAR),
  full_url VARCHAR2(255 CHAR),
  locked_flag NUMBER(1,0),
  offline_flag NUMBER(1,0),
  orig_page_id NUMBER(19,0),
  priority NUMBER(10,0),
  orig_sandbox_id NUMBER(19,0),
  page_tmplt_id NUMBER(19,0),
  sandbox_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_PAGE_FLD
--------------------------------------------------------

CREATE TABLE blc_page_fld
  (page_fld_id NUMBER(19,0),
  created_by NUMBER(19,0),
  date_created TIMESTAMP (6),
  date_updated TIMESTAMP (6),
  updated_by NUMBER(19,0),
  fld_key VARCHAR2(255 CHAR),
  lob_value CLOB,
  value VARCHAR2(255 CHAR),
  page_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_PAGE_FLD_MAP
--------------------------------------------------------

CREATE TABLE blc_page_fld_map
  (page_id NUMBER(19,0),
  page_fld_id NUMBER(19,0),
  map_key VARCHAR2(255 CHAR)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_PAGE_ITEM_CRITERIA
--------------------------------------------------------

CREATE TABLE blc_page_item_criteria
  (page_item_criteria_id NUMBER(19,0),
  order_item_match_rule CLOB,
  quantity NUMBER(10,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_PAGE_RULE
--------------------------------------------------------

CREATE TABLE blc_page_rule
  (page_rule_id NUMBER(19,0),
  match_rule CLOB
   )
/
--------------------------------------------------------
--  DDL for Table BLC_PAGE_RULE_MAP
--------------------------------------------------------

CREATE TABLE blc_page_rule_map
  (blc_page_page_id NUMBER(19,0),
  page_rule_id NUMBER(19,0),
  map_key VARCHAR2(255 CHAR)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_PAGE_TMPLT
--------------------------------------------------------

CREATE TABLE blc_page_tmplt
  (page_tmplt_id NUMBER(19,0),
  tmplt_descr VARCHAR2(255 CHAR),
  tmplt_name VARCHAR2(255 CHAR),
  tmplt_path VARCHAR2(255 CHAR),
  locale_code VARCHAR2(255 CHAR)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_PAYINFO_ADDITIONAL_FIELDS
--------------------------------------------------------

CREATE TABLE blc_payinfo_additional_fields
  (payment_id NUMBER(19,0),
  field_value VARCHAR2(255 CHAR),
  field_name VARCHAR2(255 CHAR)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_PAYMENT_ADDITIONAL_FIELDS
--------------------------------------------------------

CREATE TABLE blc_payment_additional_fields
  (payment_response_item_id NUMBER(19,0),
  field_value VARCHAR2(255 CHAR),
  field_name VARCHAR2(255 CHAR)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_PAYMENT_LOG
--------------------------------------------------------

CREATE TABLE blc_payment_log
  (payment_log_id NUMBER(19,0),
  amount_paid NUMBER(19,5),
  exception_message VARCHAR2(255 CHAR),
  log_type VARCHAR2(255 CHAR),
  order_payment_id NUMBER(19,0),
  payment_info_reference_number VARCHAR2(255 CHAR),
  transaction_success NUMBER(1,0),
  transaction_timestamp TIMESTAMP (6),
  transaction_type VARCHAR2(255 CHAR),
  user_name VARCHAR2(255 CHAR),
  currency_code VARCHAR2(255 CHAR),
  customer_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_PAYMENT_RESPONSE_ITEM
--------------------------------------------------------

CREATE TABLE blc_payment_response_item
  (payment_response_item_id NUMBER(19,0),
  amount_paid NUMBER(19,5),
  authorization_code VARCHAR2(255 CHAR),
  avs_code VARCHAR2(255 CHAR),
  implementor_response_code VARCHAR2(255 CHAR),
  implementor_response_text VARCHAR2(255 CHAR),
  middleware_response_code VARCHAR2(255 CHAR),
  middleware_response_text VARCHAR2(255 CHAR),
  order_payment_id NUMBER(19,0),
  payment_info_reference_number VARCHAR2(255 CHAR),
  processor_response_code VARCHAR2(255 CHAR),
  processor_response_text VARCHAR2(255 CHAR),
  reference_number VARCHAR2(255 CHAR),
  remaining_balance NUMBER(19,5),
  transaction_amount NUMBER(19,5),
  transaction_id VARCHAR2(255 CHAR),
  transaction_success NUMBER(1,0),
  transaction_timestamp TIMESTAMP (6),
  transaction_type VARCHAR2(255 CHAR),
  user_name VARCHAR2(255 CHAR),
  currency_code VARCHAR2(255 CHAR),
  customer_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_PERSONAL_MESSAGE
--------------------------------------------------------

CREATE TABLE blc_personal_message
  (personal_message_id NUMBER(19,0),
  message VARCHAR2(255 CHAR),
  message_from VARCHAR2(255 CHAR),
  message_to VARCHAR2(255 CHAR),
  occasion VARCHAR2(255 CHAR)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_PGTMPLT_FLDGRP_XREF
--------------------------------------------------------

CREATE TABLE blc_pgtmplt_fldgrp_xref
  (page_tmplt_id NUMBER(19,0),
  fld_group_id NUMBER(19,0),
  group_order NUMBER(10,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_PHONE
--------------------------------------------------------

CREATE TABLE blc_phone
  (phone_id NUMBER(19,0),
  is_active NUMBER(1,0),
  is_default NUMBER(1,0),
  phone_number VARCHAR2(255 CHAR)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_PRODUCT
--------------------------------------------------------

CREATE TABLE blc_product
  (product_id NUMBER(19,0),
  archived CHAR(1 CHAR),
  can_sell_without_options NUMBER(1,0),
  display_template VARCHAR2(255 CHAR),
  is_featured_product NUMBER(1,0),
  manufacture VARCHAR2(255 CHAR),
  model VARCHAR2(255 CHAR),
  tax_code VARCHAR2(255 CHAR),
  url VARCHAR2(255 CHAR),
  url_key VARCHAR2(255 CHAR),
  default_category_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_PRODUCT_ATTRIBUTE
--------------------------------------------------------

CREATE TABLE blc_product_attribute
  (product_attribute_id NUMBER(19,0),
  name VARCHAR2(255 CHAR),
  searchable NUMBER(1,0),
  value VARCHAR2(255 CHAR),
  product_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_PRODUCT_BUNDLE
--------------------------------------------------------

CREATE TABLE blc_product_bundle
  (auto_bundle NUMBER(1,0),
  bundle_promotable NUMBER(1,0),
  items_promotable NUMBER(1,0),
  pricing_model VARCHAR2(255 CHAR),
  bundle_priority NUMBER(10,0),
  product_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_PRODUCT_CROSS_SALE
--------------------------------------------------------

CREATE TABLE blc_product_cross_sale
  (cross_sale_product_id NUMBER(19,0),
  promotion_message VARCHAR2(255 CHAR),
  sequence NUMBER(19,0),
  category_id NUMBER(19,0),
  product_id NUMBER(19,0),
  related_sale_product_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_PRODUCT_FEATURED
--------------------------------------------------------

CREATE TABLE blc_product_featured
  (featured_product_id NUMBER(19,0),
  promotion_message VARCHAR2(255 CHAR),
  sequence NUMBER(19,0),
  category_id NUMBER(19,0),
  product_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_PRODUCT_OPTION
--------------------------------------------------------

CREATE TABLE blc_product_option
  (product_option_id NUMBER(19,0),
  attribute_name VARCHAR2(255 CHAR),
  display_order NUMBER(10,0),
  error_code VARCHAR2(255 CHAR),
  error_message VARCHAR2(255 CHAR),
  label VARCHAR2(255 CHAR),
  validation_type VARCHAR2(255 CHAR),
  required NUMBER(1,0),
  option_type VARCHAR2(255 CHAR),
  use_in_sku_generation NUMBER(1,0),
  validation_string VARCHAR2(255 CHAR)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_PRODUCT_OPTION_VALUE
--------------------------------------------------------

CREATE TABLE blc_product_option_value
  (product_option_value_id NUMBER(19,0),
  attribute_value VARCHAR2(255 CHAR),
  display_order NUMBER(19,0),
  price_adjustment NUMBER(19,5),
  product_option_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_PRODUCT_OPTION_XREF
--------------------------------------------------------

CREATE TABLE blc_product_option_xref
  (product_option_id NUMBER(19,0),
  product_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_PRODUCT_SKU_XREF
--------------------------------------------------------

CREATE TABLE blc_product_sku_xref
  (product_id NUMBER(19,0),
  sku_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_PRODUCT_UP_SALE
--------------------------------------------------------

CREATE TABLE blc_product_up_sale
  (up_sale_product_id NUMBER(19,0),
  promotion_message VARCHAR2(255 CHAR),
  sequence NUMBER(19,0),
  category_id NUMBER(19,0),
  product_id NUMBER(19,0),
  related_sale_product_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_QUAL_CRIT_OFFER_XREF
--------------------------------------------------------

CREATE TABLE blc_qual_crit_offer_xref
  (offer_id NUMBER(19,0),
  offer_item_criteria_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_QUAL_CRIT_PAGE_XREF
--------------------------------------------------------

CREATE TABLE blc_qual_crit_page_xref
  (page_id NUMBER(19,0),
  page_item_criteria_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_QUAL_CRIT_SC_XREF
--------------------------------------------------------

CREATE TABLE blc_qual_crit_sc_xref
  (sc_id NUMBER(19,0),
  sc_item_criteria_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_RATING_DETAIL
--------------------------------------------------------

CREATE TABLE blc_rating_detail
  (rating_detail_id NUMBER(19,0),
  rating FLOAT(126),
  rating_submitted_date TIMESTAMP (6),
  customer_id NUMBER(19,0),
  rating_summary_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_RATING_SUMMARY
--------------------------------------------------------

CREATE TABLE blc_rating_summary
  (rating_summary_id NUMBER(19,0),
  average_rating FLOAT(126),
  item_id VARCHAR2(255 CHAR),
  rating_type VARCHAR2(255 CHAR)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_REVIEW_DETAIL
--------------------------------------------------------

CREATE TABLE blc_review_detail
  (review_detail_id NUMBER(19,0),
  helpful_count NUMBER(10,0),
  not_helpful_count NUMBER(10,0),
  review_submitted_date TIMESTAMP (6),
  review_status VARCHAR2(255 CHAR),
  review_text VARCHAR2(255 CHAR),
  customer_id NUMBER(19,0),
  rating_detail_id NUMBER(19,0),
  rating_summary_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_REVIEW_FEEDBACK
--------------------------------------------------------

CREATE TABLE blc_review_feedback
  (review_feedback_id NUMBER(19,0),
  is_helpful NUMBER(1,0),
  customer_id NUMBER(19,0),
  review_detail_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_ROLE
--------------------------------------------------------

CREATE TABLE blc_role
  (role_id NUMBER(19,0),
  role_name VARCHAR2(255 CHAR)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_SANDBOX
--------------------------------------------------------

CREATE TABLE blc_sandbox
  (sandbox_id NUMBER(19,0),
  author NUMBER(19,0),
  sandbox_name VARCHAR2(255 CHAR),
  sandbox_type VARCHAR2(255 CHAR)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_SANDBOX_ACTION
--------------------------------------------------------

CREATE TABLE blc_sandbox_action
  (sandbox_action_id NUMBER(19,0),
  created_by NUMBER(19,0),
  date_created TIMESTAMP (6),
  date_updated TIMESTAMP (6),
  updated_by NUMBER(19,0),
  action_comment VARCHAR2(255 CHAR),
  action_type VARCHAR2(255 CHAR)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_SANDBOX_ITEM
--------------------------------------------------------

CREATE TABLE blc_sandbox_item
  (sandbox_item_id NUMBER(19,0),
  archived_flag CHAR(1 CHAR),
  created_by NUMBER(19,0),
  date_created TIMESTAMP (6),
  date_updated TIMESTAMP (6),
  updated_by NUMBER(19,0),
  description VARCHAR2(255 CHAR),
  grp_description VARCHAR2(255 CHAR),
  original_item_id NUMBER(19,0),
  orig_sandbox_id NUMBER(19,0),
  sandbox_id NUMBER(19,0),
  sandbox_item_type VARCHAR2(255 CHAR),
  sandbox_operation_type VARCHAR2(255 CHAR),
  temporary_item_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_SC
--------------------------------------------------------

CREATE TABLE blc_sc
  (sc_id NUMBER(19,0),
  archived_flag NUMBER(1,0),
  created_by NUMBER(19,0),
  date_created TIMESTAMP (6),
  date_updated TIMESTAMP (6),
  updated_by NUMBER(19,0),
  content_name VARCHAR2(255 CHAR),
  deleted_flag NUMBER(1,0),
  locked_flag NUMBER(1,0),
  offline_flag NUMBER(1,0),
  orig_item_id NUMBER(19,0),
  priority NUMBER(10,0),
  locale_code VARCHAR2(255 CHAR),
  orig_sandbox_id NUMBER(19,0),
  sandbox_id NUMBER(19,0),
  sc_type_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_SC_FLD
--------------------------------------------------------

CREATE TABLE blc_sc_fld
  (sc_fld_id NUMBER(19,0),
  created_by NUMBER(19,0),
  date_created TIMESTAMP (6),
  date_updated TIMESTAMP (6),
  updated_by NUMBER(19,0),
  fld_key VARCHAR2(255 CHAR),
  lob_value CLOB,
  value VARCHAR2(255 CHAR),
  sc_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_SC_FLDGRP_XREF
--------------------------------------------------------

CREATE TABLE blc_sc_fldgrp_xref
  (sc_fld_tmplt_id NUMBER(19,0),
  fld_group_id NUMBER(19,0),
  group_order NUMBER(10,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_SC_FLD_MAP
--------------------------------------------------------

CREATE TABLE blc_sc_fld_map
  (sc_id NUMBER(19,0),
  sc_fld_id NUMBER(19,0),
  map_key VARCHAR2(255 CHAR)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_SC_FLD_TMPLT
--------------------------------------------------------

CREATE TABLE blc_sc_fld_tmplt
  (sc_fld_tmplt_id NUMBER(19,0),
  name VARCHAR2(255 CHAR)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_SC_ITEM_CRITERIA
--------------------------------------------------------

CREATE TABLE blc_sc_item_criteria
  (sc_item_criteria_id NUMBER(19,0),
  order_item_match_rule CLOB,
  quantity NUMBER(10,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_SC_RULE
--------------------------------------------------------

CREATE TABLE blc_sc_rule
  (sc_rule_id NUMBER(19,0),
  match_rule CLOB
   )
/
--------------------------------------------------------
--  DDL for Table BLC_SC_RULE_MAP
--------------------------------------------------------

CREATE TABLE blc_sc_rule_map
  (blc_sc_sc_id NUMBER(19,0),
  sc_rule_id NUMBER(19,0),
  map_key VARCHAR2(255 CHAR)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_SC_TYPE
--------------------------------------------------------

CREATE TABLE blc_sc_type
  (sc_type_id NUMBER(19,0),
  description VARCHAR2(255 CHAR),
  name VARCHAR2(255 CHAR),
  sc_fld_tmplt_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_SEARCH_FACET
--------------------------------------------------------

CREATE TABLE blc_search_facet
  (search_facet_id NUMBER(19,0),
  multiselect NUMBER(1,0),
  label VARCHAR2(255 CHAR),
  requires_all_dependent NUMBER(1,0),
  search_display_priority NUMBER(10,0),
  show_on_search NUMBER(1,0),
  field_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_SEARCH_FACET_RANGE
--------------------------------------------------------

CREATE TABLE blc_search_facet_range
  (search_facet_range_id NUMBER(19,0),
  max_value NUMBER(19,5),
  min_value NUMBER(19,5),
  search_facet_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_SEARCH_FACET_XREF
--------------------------------------------------------

CREATE TABLE blc_search_facet_xref
  (id NUMBER(19,0),
  required_facet_id NUMBER(19,0),
  search_facet_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_SEARCH_INTERCEPT
--------------------------------------------------------

CREATE TABLE blc_search_intercept
  (search_redirect_id NUMBER(19,0),
  active_end_date TIMESTAMP (6),
  active_start_date TIMESTAMP (6),
  priority NUMBER(10,0),
  search_term VARCHAR2(255 CHAR),
  url VARCHAR2(255 CHAR)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_SEARCH_SYNONYM
--------------------------------------------------------

CREATE TABLE blc_search_synonym
  (search_synonym_id NUMBER(19,0),
  synonyms VARCHAR2(255 CHAR),
  term VARCHAR2(255 CHAR)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_SHIPPING_RATE
--------------------------------------------------------

CREATE TABLE blc_shipping_rate
  (id NUMBER(19,0),
  band_result_pct NUMBER(10,0),
  band_result_qty NUMBER(19,2),
  band_unit_qty NUMBER(19,2),
  fee_band NUMBER(10,0),
  fee_sub_type VARCHAR2(255 CHAR),
  fee_type VARCHAR2(255 CHAR)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_SITE
--------------------------------------------------------

CREATE TABLE blc_site
  (site_id NUMBER(19,0),
  archived CHAR(1 CHAR),
  deactivated NUMBER(1,0),
  name VARCHAR2(255 CHAR),
  site_identifier_type VARCHAR2(255 CHAR),
  site_identifier_value VARCHAR2(255 CHAR),
  production_sandbox_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_SITE_CATALOG
--------------------------------------------------------

CREATE TABLE blc_site_catalog
  (catalog_id NUMBER(19,0),
  site_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_SITE_SANDBOX
--------------------------------------------------------

CREATE TABLE blc_site_sandbox
  (site_id NUMBER(19,0),
  sandbox_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_SKU
--------------------------------------------------------

CREATE TABLE blc_sku
  (sku_id NUMBER(19,0),
  active_end_date TIMESTAMP (6),
  active_start_date TIMESTAMP (6),
  available_flag CHAR(1 CHAR),
  description VARCHAR2(255 CHAR),
  container_shape VARCHAR2(255 CHAR),
  depth NUMBER(19,2),
  dimension_unit_of_measure VARCHAR2(255 CHAR),
  girth NUMBER(19,2),
  height NUMBER(19,2),
  container_size VARCHAR2(255 CHAR),
  width NUMBER(19,2),
  discountable_flag CHAR(1 CHAR),
  fulfillment_type VARCHAR2(255 CHAR),
  inventory_type VARCHAR2(255 CHAR),
  is_machine_sortable NUMBER(1,0),
  long_description CLOB,
  name VARCHAR2(255 CHAR),
  retail_price NUMBER(19,5),
  sale_price NUMBER(19,5),
  tax_code VARCHAR2(255 CHAR),
  taxable_flag CHAR(1 CHAR),
  weight NUMBER(19,2),
  weight_unit_of_measure VARCHAR2(255 CHAR),
  currency_code VARCHAR2(255 CHAR),
  default_product_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_SKU_ATTRIBUTE
--------------------------------------------------------

CREATE TABLE blc_sku_attribute
  (sku_attr_id NUMBER(19,0),
  name VARCHAR2(255 CHAR),
  searchable NUMBER(1,0),
  value VARCHAR2(255 CHAR),
  sku_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_SKU_AVAILABILITY
--------------------------------------------------------

CREATE TABLE blc_sku_availability
  (sku_availability_id NUMBER(19,0),
  availability_date TIMESTAMP (6),
  availability_status VARCHAR2(255 CHAR),
  location_id NUMBER(19,0),
  qty_on_hand NUMBER(10,0),
  reserve_qty NUMBER(10,0),
  sku_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_SKU_BUNDLE_ITEM
--------------------------------------------------------

CREATE TABLE blc_sku_bundle_item
  (sku_bundle_item_id NUMBER(19,0),
  item_sale_price NUMBER(19,5),
  quantity NUMBER(10,0),
  product_bundle_id NUMBER(19,0),
  sku_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_SKU_FEE
--------------------------------------------------------

CREATE TABLE blc_sku_fee
  (sku_fee_id NUMBER(19,0),
  amount NUMBER(19,5),
  description VARCHAR2(255 CHAR),
  expression CLOB,
  fee_type VARCHAR2(255 CHAR),
  name VARCHAR2(255 CHAR),
  taxable NUMBER(1,0),
  currency_code VARCHAR2(255 CHAR)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_SKU_FEE_XREF
--------------------------------------------------------

CREATE TABLE blc_sku_fee_xref
  (sku_fee_id NUMBER(19,0),
  sku_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_SKU_FULFILLMENT_EXCLUDED
--------------------------------------------------------

CREATE TABLE blc_sku_fulfillment_excluded
  (sku_id NUMBER(19,0),
  fulfillment_option_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_SKU_FULFILLMENT_FLAT_RATES
--------------------------------------------------------

CREATE TABLE blc_sku_fulfillment_flat_rates
  (sku_id NUMBER(19,0),
  rate NUMBER(19,5),
  fulfillment_option_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_SKU_MEDIA_MAP
--------------------------------------------------------

CREATE TABLE blc_sku_media_map
  (blc_sku_sku_id NUMBER(19,0),
  media_id NUMBER(19,0),
  map_key VARCHAR2(255 CHAR)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_SKU_OPTION_VALUE_XREF
--------------------------------------------------------

CREATE TABLE blc_sku_option_value_xref
  (sku_id NUMBER(19,0),
  product_option_value_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_STATE
--------------------------------------------------------

CREATE TABLE blc_state
  (abbreviation VARCHAR2(255 CHAR),
  name VARCHAR2(255 CHAR),
  country VARCHAR2(255 CHAR)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_STATIC_ASSET
--------------------------------------------------------

CREATE TABLE blc_static_asset
  (static_asset_id NUMBER(19,0),
  alt_text VARCHAR2(255 CHAR),
  archived_flag NUMBER(1,0),
  created_by NUMBER(19,0),
  date_created TIMESTAMP (6),
  date_updated TIMESTAMP (6),
  updated_by NUMBER(19,0),
  deleted_flag NUMBER(1,0),
  file_extension VARCHAR2(255 CHAR),
  file_size NUMBER(19,0),
  full_url VARCHAR2(255 CHAR),
  locked_flag NUMBER(1,0),
  mime_type VARCHAR2(255 CHAR),
  name VARCHAR2(255 CHAR),
  orig_asset_id NUMBER(19,0),
  storage_type VARCHAR2(255 CHAR),
  title VARCHAR2(255 CHAR),
  orig_sandbox_id NUMBER(19,0),
  sandbox_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_STATIC_ASSET_DESC
--------------------------------------------------------

CREATE TABLE blc_static_asset_desc
  (static_asset_desc_id NUMBER(19,0),
  created_by NUMBER(19,0),
  date_created TIMESTAMP (6),
  date_updated TIMESTAMP (6),
  updated_by NUMBER(19,0),
  description VARCHAR2(255 CHAR),
  long_description VARCHAR2(255 CHAR)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_STATIC_ASSET_STRG
--------------------------------------------------------

CREATE TABLE blc_static_asset_strg
  (static_asset_strg_id NUMBER(19,0),
  file_data BLOB,
  static_asset_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_STORE
--------------------------------------------------------

CREATE TABLE blc_store
  (store_id VARCHAR2(255 CHAR),
  address_1 VARCHAR2(255 CHAR),
  address_2 VARCHAR2(255 CHAR),
  store_city VARCHAR2(255 CHAR),
  store_country VARCHAR2(255 CHAR),
  latitude FLOAT(126),
  longitude FLOAT(126),
  store_name VARCHAR2(255 CHAR),
  store_phone VARCHAR2(255 CHAR),
  store_state VARCHAR2(255 CHAR),
  store_zip VARCHAR2(255 CHAR)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_SYSTEM_PROPERTY
--------------------------------------------------------

CREATE TABLE blc_system_property
  (blc_system_property_id NUMBER(19,0),
  property_name VARCHAR2(255 CHAR),
  property_value VARCHAR2(255 CHAR)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_TAR_CRIT_OFFER_XREF
--------------------------------------------------------

CREATE TABLE blc_tar_crit_offer_xref
  (offer_id NUMBER(19,0),
  offer_item_criteria_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_TAX_DETAIL
--------------------------------------------------------

CREATE TABLE blc_tax_detail
  (tax_detail_id NUMBER(19,0),
  amount NUMBER(19,5),
  tax_country VARCHAR2(255 CHAR),
  jurisdiction_name VARCHAR2(255 CHAR),
  rate NUMBER(19,5),
  tax_region VARCHAR2(255 CHAR),
  tax_name VARCHAR2(255 CHAR),
  type VARCHAR2(255 CHAR),
  currency_code VARCHAR2(255 CHAR),
  module_config_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_TRANSLATION
--------------------------------------------------------

CREATE TABLE blc_translation
  (translation_id NUMBER(19,0),
  entity_id VARCHAR2(255 CHAR),
  entity_type VARCHAR2(255 CHAR),
  field_name VARCHAR2(255 CHAR),
  locale_code VARCHAR2(255 CHAR),
  translated_value CLOB
   )
/
--------------------------------------------------------
--  DDL for Table BLC_URL_HANDLER
--------------------------------------------------------

CREATE TABLE blc_url_handler
  (url_handler_id NUMBER(19,0),
  incoming_url VARCHAR2(255 CHAR),
  new_url VARCHAR2(255 CHAR),
  url_redirect_type VARCHAR2(255 CHAR)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_USERCONNECTION
--------------------------------------------------------

CREATE TABLE blc_userconnection
  (providerid VARCHAR2(255 CHAR),
  provideruserid VARCHAR2(255 CHAR),
  userid VARCHAR2(255 CHAR),
  accesstoken VARCHAR2(255 CHAR),
  displayname VARCHAR2(255 CHAR),
  expiretime NUMBER(19,0),
  imageurl VARCHAR2(255 CHAR),
  profileurl VARCHAR2(255 CHAR),
  rank NUMBER(10,0),
  refreshtoken VARCHAR2(255 CHAR),
  secret VARCHAR2(255 CHAR)
   )
/
--------------------------------------------------------
--  DDL for Table BLC_ZIP_CODE
--------------------------------------------------------

CREATE TABLE blc_zip_code
  (zip_code_id VARCHAR2(255 CHAR),
  zip_city VARCHAR2(255 CHAR),
  zip_latitude FLOAT(126),
  zip_longitude FLOAT(126),
  zip_state VARCHAR2(255 CHAR),
  zipcode NUMBER(10,0)
   )
/
--------------------------------------------------------
--  DDL for Table SANDBOX_ITEM_ACTION
--------------------------------------------------------

CREATE TABLE sandbox_item_action
  (sandbox_action_id NUMBER(19,0),
  sandbox_item_id NUMBER(19,0)
   )
/
--------------------------------------------------------
--  DDL for Table SEQUENCE_GENERATOR
--------------------------------------------------------

CREATE TABLE sequence_generator
  (id_name VARCHAR2(255 CHAR),
  id_val NUMBER(19,0)
   )
/
--------------------------------------------------------
--  Constraints for Table BLC_ADDITIONAL_OFFER_INFO
--------------------------------------------------------

ALTER TABLE blc_additional_offer_info add primary key (blc_order_order_id, offer_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_additional_offer_info modify (offer_id NOT NULL ENABLE)
/
ALTER TABLE blc_additional_offer_info modify (offer_info_id NOT NULL ENABLE)
/
ALTER TABLE blc_additional_offer_info modify (blc_order_order_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_ADDRESS
--------------------------------------------------------

ALTER TABLE blc_address add primary key (address_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_address modify (country NOT NULL ENABLE)
/
ALTER TABLE blc_address modify (postal_code NOT NULL ENABLE)
/
ALTER TABLE blc_address modify (city NOT NULL ENABLE)
/
ALTER TABLE blc_address modify (address_line1 NOT NULL ENABLE)
/
ALTER TABLE blc_address modify (address_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_ADMIN_MODULE
--------------------------------------------------------

ALTER TABLE blc_admin_module add primary key (admin_module_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_admin_module modify (name NOT NULL ENABLE)
/
ALTER TABLE blc_admin_module modify (module_key NOT NULL ENABLE)
/
ALTER TABLE blc_admin_module modify (admin_module_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_ADMIN_PASSWORD_TOKEN
--------------------------------------------------------

ALTER TABLE blc_admin_password_token add primary key (password_token)
  USING INDEX  ENABLE
/
ALTER TABLE blc_admin_password_token modify (token_used_flag NOT NULL ENABLE)
/
ALTER TABLE blc_admin_password_token modify (create_date NOT NULL ENABLE)
/
ALTER TABLE blc_admin_password_token modify (admin_user_id NOT NULL ENABLE)
/
ALTER TABLE blc_admin_password_token modify (password_token NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_ADMIN_PERMISSION
--------------------------------------------------------

ALTER TABLE blc_admin_permission add primary key (admin_permission_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_admin_permission modify (permission_type NOT NULL ENABLE)
/
ALTER TABLE blc_admin_permission modify (name NOT NULL ENABLE)
/
ALTER TABLE blc_admin_permission modify (description NOT NULL ENABLE)
/
ALTER TABLE blc_admin_permission modify (admin_permission_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_ADMIN_PERMISSION_ENTITY
--------------------------------------------------------

ALTER TABLE blc_admin_permission_entity add primary key (admin_permission_entity_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_admin_permission_entity modify (ceiling_entity NOT NULL ENABLE)
/
ALTER TABLE blc_admin_permission_entity modify (admin_permission_entity_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_ADMIN_ROLE
--------------------------------------------------------

ALTER TABLE blc_admin_role add primary key (admin_role_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_admin_role modify (name NOT NULL ENABLE)
/
ALTER TABLE blc_admin_role modify (description NOT NULL ENABLE)
/
ALTER TABLE blc_admin_role modify (admin_role_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_ADMIN_ROLE_PERMISSION_XREF
--------------------------------------------------------

ALTER TABLE blc_admin_role_permission_xref add primary key (admin_permission_id, admin_role_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_admin_role_permission_xref modify (admin_permission_id NOT NULL ENABLE)
/
ALTER TABLE blc_admin_role_permission_xref modify (admin_role_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_ADMIN_SECTION
--------------------------------------------------------

ALTER TABLE blc_admin_section add constraint section_key_ unique (section_key)
  USING INDEX  ENABLE
/
ALTER TABLE blc_admin_section add primary key (admin_section_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_admin_section modify (admin_module_id NOT NULL ENABLE)
/
ALTER TABLE blc_admin_section modify (use_default_handler NOT NULL ENABLE)
/
ALTER TABLE blc_admin_section modify (section_key NOT NULL ENABLE)
/
ALTER TABLE blc_admin_section modify (name NOT NULL ENABLE)
/
ALTER TABLE blc_admin_section modify (admin_section_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_ADMIN_SEC_PERM_XREF
--------------------------------------------------------

ALTER TABLE blc_admin_sec_perm_xref modify (admin_permission_id NOT NULL ENABLE)
/
ALTER TABLE blc_admin_sec_perm_xref modify (admin_section_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_ADMIN_USER
--------------------------------------------------------

ALTER TABLE blc_admin_user add primary key (admin_user_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_admin_user modify (name NOT NULL ENABLE)
/
ALTER TABLE blc_admin_user modify (login NOT NULL ENABLE)
/
ALTER TABLE blc_admin_user modify (email NOT NULL ENABLE)
/
ALTER TABLE blc_admin_user modify (admin_user_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_ADMIN_USER_PERMISSION_XREF
--------------------------------------------------------

ALTER TABLE blc_admin_user_permission_xref add primary key (admin_permission_id, admin_user_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_admin_user_permission_xref modify (admin_permission_id NOT NULL ENABLE)
/
ALTER TABLE blc_admin_user_permission_xref modify (admin_user_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_ADMIN_USER_ROLE_XREF
--------------------------------------------------------

ALTER TABLE blc_admin_user_role_xref add primary key (admin_role_id, admin_user_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_admin_user_role_xref modify (admin_role_id NOT NULL ENABLE)
/
ALTER TABLE blc_admin_user_role_xref modify (admin_user_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_ADMIN_USER_SANDBOX
--------------------------------------------------------

ALTER TABLE blc_admin_user_sandbox add primary key (admin_user_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_admin_user_sandbox modify (admin_user_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_AMOUNT_ITEM
--------------------------------------------------------

ALTER TABLE blc_amount_item add primary key (amount_item_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_amount_item modify (unit_price NOT NULL ENABLE)
/
ALTER TABLE blc_amount_item modify (quantity NOT NULL ENABLE)
/
ALTER TABLE blc_amount_item modify (amount_item_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_ASSET_DESC_MAP
--------------------------------------------------------

ALTER TABLE blc_asset_desc_map add primary key (static_asset_id, map_key)
  USING INDEX  ENABLE
/
ALTER TABLE blc_asset_desc_map modify (map_key NOT NULL ENABLE)
/
ALTER TABLE blc_asset_desc_map modify (static_asset_desc_id NOT NULL ENABLE)
/
ALTER TABLE blc_asset_desc_map modify (static_asset_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_BANK_ACCOUNT_PAYMENT
--------------------------------------------------------

ALTER TABLE blc_bank_account_payment add primary key (payment_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_bank_account_payment modify (routing_number NOT NULL ENABLE)
/
ALTER TABLE blc_bank_account_payment modify (reference_number NOT NULL ENABLE)
/
ALTER TABLE blc_bank_account_payment modify (account_number NOT NULL ENABLE)
/
ALTER TABLE blc_bank_account_payment modify (payment_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_BUNDLE_ORDER_ITEM
--------------------------------------------------------

ALTER TABLE blc_bundle_order_item add primary key (order_item_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_bundle_order_item modify (order_item_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_BUND_ITEM_FEE_PRICE
--------------------------------------------------------

ALTER TABLE blc_bund_item_fee_price add primary key (bund_item_fee_price_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_bund_item_fee_price modify (bund_order_item_id NOT NULL ENABLE)
/
ALTER TABLE blc_bund_item_fee_price modify (bund_item_fee_price_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_CANDIDATE_FG_OFFER
--------------------------------------------------------

ALTER TABLE blc_candidate_fg_offer add primary key (candidate_fg_offer_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_candidate_fg_offer modify (offer_id NOT NULL ENABLE)
/
ALTER TABLE blc_candidate_fg_offer modify (candidate_fg_offer_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_CANDIDATE_ITEM_OFFER
--------------------------------------------------------

ALTER TABLE blc_candidate_item_offer add primary key (candidate_item_offer_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_candidate_item_offer modify (offer_id NOT NULL ENABLE)
/
ALTER TABLE blc_candidate_item_offer modify (candidate_item_offer_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_CANDIDATE_ORDER_OFFER
--------------------------------------------------------

ALTER TABLE blc_candidate_order_offer add primary key (candidate_order_offer_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_candidate_order_offer modify (offer_id NOT NULL ENABLE)
/
ALTER TABLE blc_candidate_order_offer modify (candidate_order_offer_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_CATALOG
--------------------------------------------------------

ALTER TABLE blc_catalog add primary key (catalog_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_catalog modify (catalog_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_CATEGORY
--------------------------------------------------------

ALTER TABLE blc_category add primary key (category_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_category modify (name NOT NULL ENABLE)
/
ALTER TABLE blc_category modify (category_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_CATEGORY_ATTRIBUTE
--------------------------------------------------------

ALTER TABLE blc_category_attribute add primary key (category_attribute_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_category_attribute modify (category_id NOT NULL ENABLE)
/
ALTER TABLE blc_category_attribute modify (name NOT NULL ENABLE)
/
ALTER TABLE blc_category_attribute modify (category_attribute_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_CATEGORY_IMAGE
--------------------------------------------------------

ALTER TABLE blc_category_image add primary key (category_id, name)
  USING INDEX  ENABLE
/
ALTER TABLE blc_category_image modify (name NOT NULL ENABLE)
/
ALTER TABLE blc_category_image modify (category_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_CATEGORY_MEDIA_MAP
--------------------------------------------------------

ALTER TABLE blc_category_media_map add primary key (blc_category_category_id, map_key)
  USING INDEX  ENABLE
/
ALTER TABLE blc_category_media_map modify (map_key NOT NULL ENABLE)
/
ALTER TABLE blc_category_media_map modify (media_id NOT NULL ENABLE)
/
ALTER TABLE blc_category_media_map modify (blc_category_category_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_CATEGORY_PRODUCT_XREF
--------------------------------------------------------

ALTER TABLE blc_category_product_xref add primary key (category_id, product_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_category_product_xref modify (product_id NOT NULL ENABLE)
/
ALTER TABLE blc_category_product_xref modify (category_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_CATEGORY_XREF
--------------------------------------------------------

ALTER TABLE blc_category_xref add primary key (category_id, sub_category_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_category_xref modify (category_id NOT NULL ENABLE)
/
ALTER TABLE blc_category_xref modify (sub_category_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_CAT_SEARCH_FACET_EXCL_XREF
--------------------------------------------------------

ALTER TABLE blc_cat_search_facet_excl_xref add primary key (cat_excl_search_facet_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_cat_search_facet_excl_xref modify (cat_excl_search_facet_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_CAT_SEARCH_FACET_XREF
--------------------------------------------------------

ALTER TABLE blc_cat_search_facet_xref add primary key (category_search_facet_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_cat_search_facet_xref modify (category_search_facet_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_CHALLENGE_QUESTION
--------------------------------------------------------

ALTER TABLE blc_challenge_question add primary key (question_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_challenge_question modify (question NOT NULL ENABLE)
/
ALTER TABLE blc_challenge_question modify (question_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_CODE_TYPES
--------------------------------------------------------

ALTER TABLE blc_code_types add primary key (code_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_code_types modify (code_key NOT NULL ENABLE)
/
ALTER TABLE blc_code_types modify (code_type NOT NULL ENABLE)
/
ALTER TABLE blc_code_types modify (code_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_COUNTRY
--------------------------------------------------------

ALTER TABLE blc_country add primary key (abbreviation)
  USING INDEX  ENABLE
/
ALTER TABLE blc_country modify (name NOT NULL ENABLE)
/
ALTER TABLE blc_country modify (abbreviation NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_CREDIT_CARD_PAYMENT
--------------------------------------------------------

ALTER TABLE blc_credit_card_payment add primary key (payment_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_credit_card_payment modify (reference_number NOT NULL ENABLE)
/
ALTER TABLE blc_credit_card_payment modify (pan NOT NULL ENABLE)
/
ALTER TABLE blc_credit_card_payment modify (name_on_card NOT NULL ENABLE)
/
ALTER TABLE blc_credit_card_payment modify (expiration_year NOT NULL ENABLE)
/
ALTER TABLE blc_credit_card_payment modify (expiration_month NOT NULL ENABLE)
/
ALTER TABLE blc_credit_card_payment modify (payment_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_CURRENCY
--------------------------------------------------------

ALTER TABLE blc_currency add primary key (currency_code)
  USING INDEX  ENABLE
/
ALTER TABLE blc_currency modify (currency_code NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_CUSTOMER
--------------------------------------------------------

ALTER TABLE blc_customer add primary key (customer_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_customer modify (customer_id NOT NULL ENABLE)
/
ALTER TABLE blc_customer add constraint key1 unique (user_name)
  USING INDEX  ENABLE
/
--------------------------------------------------------
--  Constraints for Table BLC_CUSTOMER_ADDRESS
--------------------------------------------------------

ALTER TABLE blc_customer_address add primary key (customer_address_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_customer_address modify (customer_id NOT NULL ENABLE)
/
ALTER TABLE blc_customer_address modify (address_id NOT NULL ENABLE)
/
ALTER TABLE blc_customer_address modify (customer_address_id NOT NULL ENABLE)
/
ALTER TABLE blc_customer_address add constraint cstmr_addr_unique_cnstrnt unique (customer_id, address_name)
  USING INDEX  ENABLE
/
--------------------------------------------------------
--  Constraints for Table BLC_CUSTOMER_ATTRIBUTE
--------------------------------------------------------

ALTER TABLE blc_customer_attribute add primary key (customer_attr_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_customer_attribute modify (customer_id NOT NULL ENABLE)
/
ALTER TABLE blc_customer_attribute modify (name NOT NULL ENABLE)
/
ALTER TABLE blc_customer_attribute modify (customer_attr_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_CUSTOMER_OFFER_XREF
--------------------------------------------------------

ALTER TABLE blc_customer_offer_xref add primary key (customer_offer_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_customer_offer_xref modify (offer_id NOT NULL ENABLE)
/
ALTER TABLE blc_customer_offer_xref modify (customer_id NOT NULL ENABLE)
/
ALTER TABLE blc_customer_offer_xref modify (customer_offer_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_CUSTOMER_PASSWORD_TOKEN
--------------------------------------------------------

ALTER TABLE blc_customer_password_token add primary key (password_token)
  USING INDEX  ENABLE
/
ALTER TABLE blc_customer_password_token modify (token_used_flag NOT NULL ENABLE)
/
ALTER TABLE blc_customer_password_token modify (customer_id NOT NULL ENABLE)
/
ALTER TABLE blc_customer_password_token modify (create_date NOT NULL ENABLE)
/
ALTER TABLE blc_customer_password_token modify (password_token NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_CUSTOMER_PAYMENT
--------------------------------------------------------

ALTER TABLE blc_customer_payment add primary key (customer_payment_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_customer_payment modify (customer_id NOT NULL ENABLE)
/
ALTER TABLE blc_customer_payment modify (address_id NOT NULL ENABLE)
/
ALTER TABLE blc_customer_payment modify (customer_payment_id NOT NULL ENABLE)
/
ALTER TABLE blc_customer_payment add constraint cstmr_pay_unique_cnstrnt unique (customer_id, payment_token)
  USING INDEX  ENABLE
/
--------------------------------------------------------
--  Constraints for Table BLC_CUSTOMER_PAYMENT_FIELDS
--------------------------------------------------------

ALTER TABLE blc_customer_payment_fields add primary key (customer_payment_id, field_name)
  USING INDEX  ENABLE
/
ALTER TABLE blc_customer_payment_fields modify (field_name NOT NULL ENABLE)
/
ALTER TABLE blc_customer_payment_fields modify (customer_payment_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_CUSTOMER_PHONE
--------------------------------------------------------

ALTER TABLE blc_customer_phone add primary key (customer_phone_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_customer_phone modify (phone_id NOT NULL ENABLE)
/
ALTER TABLE blc_customer_phone modify (customer_id NOT NULL ENABLE)
/
ALTER TABLE blc_customer_phone modify (customer_phone_id NOT NULL ENABLE)
/
ALTER TABLE blc_customer_phone add constraint cstmr_phone_unique_cnstrnt unique (customer_id, phone_name)
  USING INDEX  ENABLE
/
--------------------------------------------------------
--  Constraints for Table BLC_CUSTOMER_ROLE
--------------------------------------------------------

ALTER TABLE blc_customer_role add primary key (customer_role_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_customer_role modify (role_id NOT NULL ENABLE)
/
ALTER TABLE blc_customer_role modify (customer_id NOT NULL ENABLE)
/
ALTER TABLE blc_customer_role modify (customer_role_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_DATA_DRVN_ENUM
--------------------------------------------------------

ALTER TABLE blc_data_drvn_enum add primary key (enum_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_data_drvn_enum modify (enum_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_DATA_DRVN_ENUM_VAL
--------------------------------------------------------

ALTER TABLE blc_data_drvn_enum_val add primary key (enum_val_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_data_drvn_enum_val modify (enum_val_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_DISCRETE_ORDER_ITEM
--------------------------------------------------------

ALTER TABLE blc_discrete_order_item add primary key (order_item_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_discrete_order_item modify (sku_id NOT NULL ENABLE)
/
ALTER TABLE blc_discrete_order_item modify (order_item_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_DISC_ITEM_FEE_PRICE
--------------------------------------------------------

ALTER TABLE blc_disc_item_fee_price add primary key (disc_item_fee_price_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_disc_item_fee_price modify (order_item_id NOT NULL ENABLE)
/
ALTER TABLE blc_disc_item_fee_price modify (disc_item_fee_price_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_DYN_DISCRETE_ORDER_ITEM
--------------------------------------------------------

ALTER TABLE blc_dyn_discrete_order_item add primary key (order_item_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_dyn_discrete_order_item modify (order_item_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_EMAIL_TRACKING
--------------------------------------------------------

ALTER TABLE blc_email_tracking add primary key (email_tracking_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_email_tracking modify (email_tracking_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_EMAIL_TRACKING_CLICKS
--------------------------------------------------------

ALTER TABLE blc_email_tracking_clicks add primary key (click_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_email_tracking_clicks modify (email_tracking_id NOT NULL ENABLE)
/
ALTER TABLE blc_email_tracking_clicks modify (date_clicked NOT NULL ENABLE)
/
ALTER TABLE blc_email_tracking_clicks modify (click_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_EMAIL_TRACKING_OPENS
--------------------------------------------------------

ALTER TABLE blc_email_tracking_opens add primary key (open_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_email_tracking_opens modify (open_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_FG_ADJUSTMENT
--------------------------------------------------------

ALTER TABLE blc_fg_adjustment add primary key (fg_adjustment_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_fg_adjustment modify (offer_id NOT NULL ENABLE)
/
ALTER TABLE blc_fg_adjustment modify (adjustment_value NOT NULL ENABLE)
/
ALTER TABLE blc_fg_adjustment modify (adjustment_reason NOT NULL ENABLE)
/
ALTER TABLE blc_fg_adjustment modify (fg_adjustment_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_FG_FEE_TAX_XREF
--------------------------------------------------------

ALTER TABLE blc_fg_fee_tax_xref modify (tax_detail_id NOT NULL ENABLE)
/
ALTER TABLE blc_fg_fee_tax_xref modify (fulfillment_group_fee_id NOT NULL ENABLE)
/
ALTER TABLE blc_fg_fee_tax_xref add constraint uk25426dc0fa888c35 unique (tax_detail_id)
  USING INDEX  ENABLE
/
--------------------------------------------------------
--  Constraints for Table BLC_FG_FG_TAX_XREF
--------------------------------------------------------

ALTER TABLE blc_fg_fg_tax_xref modify (tax_detail_id NOT NULL ENABLE)
/
ALTER TABLE blc_fg_fg_tax_xref modify (fulfillment_group_id NOT NULL ENABLE)
/
ALTER TABLE blc_fg_fg_tax_xref add constraint uk61bea455fa888c35 unique (tax_detail_id)
  USING INDEX  ENABLE
/
--------------------------------------------------------
--  Constraints for Table BLC_FG_ITEM_TAX_XREF
--------------------------------------------------------

ALTER TABLE blc_fg_item_tax_xref modify (tax_detail_id NOT NULL ENABLE)
/
ALTER TABLE blc_fg_item_tax_xref modify (fulfillment_group_item_id NOT NULL ENABLE)
/
ALTER TABLE blc_fg_item_tax_xref add constraint ukdd3e8443fa888c35 unique (tax_detail_id)
  USING INDEX  ENABLE
/
--------------------------------------------------------
--  Constraints for Table BLC_FIELD
--------------------------------------------------------

ALTER TABLE blc_field add primary key (field_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_field modify (property_name NOT NULL ENABLE)
/
ALTER TABLE blc_field modify (entity_type NOT NULL ENABLE)
/
ALTER TABLE blc_field modify (field_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_FIELD_SEARCH_TYPES
--------------------------------------------------------

ALTER TABLE blc_field_search_types modify (field_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_FLD_DEF
--------------------------------------------------------

ALTER TABLE blc_fld_def add primary key (fld_def_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_fld_def modify (fld_def_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_FLD_ENUM
--------------------------------------------------------

ALTER TABLE blc_fld_enum add primary key (fld_enum_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_fld_enum modify (fld_enum_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_FLD_ENUM_ITEM
--------------------------------------------------------

ALTER TABLE blc_fld_enum_item add primary key (fld_enum_item_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_fld_enum_item modify (fld_enum_item_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_FLD_GROUP
--------------------------------------------------------

ALTER TABLE blc_fld_group add primary key (fld_group_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_fld_group modify (fld_group_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_FULFILLMENT_GROUP
--------------------------------------------------------

ALTER TABLE blc_fulfillment_group add primary key (fulfillment_group_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_fulfillment_group modify (order_id NOT NULL ENABLE)
/
ALTER TABLE blc_fulfillment_group modify (fulfillment_group_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_FULFILLMENT_GROUP_FEE
--------------------------------------------------------

ALTER TABLE blc_fulfillment_group_fee add primary key (fulfillment_group_fee_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_fulfillment_group_fee modify (fulfillment_group_id NOT NULL ENABLE)
/
ALTER TABLE blc_fulfillment_group_fee modify (fulfillment_group_fee_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_FULFILLMENT_GROUP_ITEM
--------------------------------------------------------

ALTER TABLE blc_fulfillment_group_item add primary key (fulfillment_group_item_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_fulfillment_group_item modify (order_item_id NOT NULL ENABLE)
/
ALTER TABLE blc_fulfillment_group_item modify (fulfillment_group_id NOT NULL ENABLE)
/
ALTER TABLE blc_fulfillment_group_item modify (quantity NOT NULL ENABLE)
/
ALTER TABLE blc_fulfillment_group_item modify (fulfillment_group_item_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_FULFILLMENT_OPTION
--------------------------------------------------------

ALTER TABLE blc_fulfillment_option add primary key (fulfillment_option_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_fulfillment_option modify (fulfillment_type NOT NULL ENABLE)
/
ALTER TABLE blc_fulfillment_option modify (fulfillment_option_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_FULFILLMENT_OPTION_FIXED
--------------------------------------------------------

ALTER TABLE blc_fulfillment_option_fixed add primary key (fulfillment_option_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_fulfillment_option_fixed modify (fulfillment_option_id NOT NULL ENABLE)
/
ALTER TABLE blc_fulfillment_option_fixed modify (price NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_FULFILLMENT_OPT_BANDED_PRC
--------------------------------------------------------

ALTER TABLE blc_fulfillment_opt_banded_prc add primary key (fulfillment_option_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_fulfillment_opt_banded_prc modify (fulfillment_option_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_FULFILLMENT_OPT_BANDED_WGT
--------------------------------------------------------

ALTER TABLE blc_fulfillment_opt_banded_wgt add primary key (fulfillment_option_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_fulfillment_opt_banded_wgt modify (fulfillment_option_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_FULFILLMENT_PRICE_BAND
--------------------------------------------------------

ALTER TABLE blc_fulfillment_price_band add primary key (fulfillment_price_band_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_fulfillment_price_band modify (retail_price_minimum_amount NOT NULL ENABLE)
/
ALTER TABLE blc_fulfillment_price_band modify (result_amount_type NOT NULL ENABLE)
/
ALTER TABLE blc_fulfillment_price_band modify (result_amount NOT NULL ENABLE)
/
ALTER TABLE blc_fulfillment_price_band modify (fulfillment_price_band_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_FULFILLMENT_WEIGHT_BAND
--------------------------------------------------------

ALTER TABLE blc_fulfillment_weight_band add primary key (fulfillment_weight_band_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_fulfillment_weight_band modify (result_amount_type NOT NULL ENABLE)
/
ALTER TABLE blc_fulfillment_weight_band modify (result_amount NOT NULL ENABLE)
/
ALTER TABLE blc_fulfillment_weight_band modify (fulfillment_weight_band_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_GIFTWRAP_ORDER_ITEM
--------------------------------------------------------

ALTER TABLE blc_giftwrap_order_item add primary key (order_item_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_giftwrap_order_item modify (order_item_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_GIFT_CARD_PAYMENT
--------------------------------------------------------

ALTER TABLE blc_gift_card_payment add primary key (payment_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_gift_card_payment modify (reference_number NOT NULL ENABLE)
/
ALTER TABLE blc_gift_card_payment modify (pan NOT NULL ENABLE)
/
ALTER TABLE blc_gift_card_payment modify (payment_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_ID_GENERATION
--------------------------------------------------------

ALTER TABLE blc_id_generation add primary key (id_type)
  USING INDEX  ENABLE
/
ALTER TABLE blc_id_generation modify (batch_start NOT NULL ENABLE)
/
ALTER TABLE blc_id_generation modify (batch_size NOT NULL ENABLE)
/
ALTER TABLE blc_id_generation modify (id_type NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_IMG_STATIC_ASSET
--------------------------------------------------------

ALTER TABLE blc_img_static_asset add primary key (static_asset_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_img_static_asset modify (static_asset_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_ITEM_OFFER_QUALIFIER
--------------------------------------------------------

ALTER TABLE blc_item_offer_qualifier add primary key (item_offer_qualifier_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_item_offer_qualifier modify (offer_id NOT NULL ENABLE)
/
ALTER TABLE blc_item_offer_qualifier modify (item_offer_qualifier_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_LOCALE
--------------------------------------------------------

ALTER TABLE blc_locale add primary key (locale_code)
  USING INDEX  ENABLE
/
ALTER TABLE blc_locale modify (locale_code NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_MEDIA
--------------------------------------------------------

ALTER TABLE blc_media add primary key (media_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_media modify (url NOT NULL ENABLE)
/
ALTER TABLE blc_media modify (media_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_MODULE_CONFIGURATION
--------------------------------------------------------

ALTER TABLE blc_module_configuration add primary key (module_config_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_module_configuration modify (module_priority NOT NULL ENABLE)
/
ALTER TABLE blc_module_configuration modify (module_name NOT NULL ENABLE)
/
ALTER TABLE blc_module_configuration modify (is_default NOT NULL ENABLE)
/
ALTER TABLE blc_module_configuration modify (config_type NOT NULL ENABLE)
/
ALTER TABLE blc_module_configuration modify (module_config_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_OFFER
--------------------------------------------------------

ALTER TABLE blc_offer add primary key (offer_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_offer modify (offer_value NOT NULL ENABLE)
/
ALTER TABLE blc_offer modify (offer_type NOT NULL ENABLE)
/
ALTER TABLE blc_offer modify (offer_name NOT NULL ENABLE)
/
ALTER TABLE blc_offer modify (offer_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_OFFER_AUDIT
--------------------------------------------------------

ALTER TABLE blc_offer_audit add primary key (offer_audit_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_offer_audit modify (offer_audit_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_OFFER_CODE
--------------------------------------------------------

ALTER TABLE blc_offer_code add primary key (offer_code_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_offer_code modify (offer_id NOT NULL ENABLE)
/
ALTER TABLE blc_offer_code modify (offer_code NOT NULL ENABLE)
/
ALTER TABLE blc_offer_code modify (offer_code_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_OFFER_INFO
--------------------------------------------------------

ALTER TABLE blc_offer_info add primary key (offer_info_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_offer_info modify (offer_info_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_OFFER_INFO_FIELDS
--------------------------------------------------------

ALTER TABLE blc_offer_info_fields add primary key (offer_info_fields_id, field_name)
  USING INDEX  ENABLE
/
ALTER TABLE blc_offer_info_fields modify (field_name NOT NULL ENABLE)
/
ALTER TABLE blc_offer_info_fields modify (offer_info_fields_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_OFFER_ITEM_CRITERIA
--------------------------------------------------------

ALTER TABLE blc_offer_item_criteria add primary key (offer_item_criteria_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_offer_item_criteria modify (quantity NOT NULL ENABLE)
/
ALTER TABLE blc_offer_item_criteria modify (offer_item_criteria_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_OFFER_RULE
--------------------------------------------------------

ALTER TABLE blc_offer_rule add primary key (offer_rule_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_offer_rule modify (offer_rule_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_OFFER_RULE_MAP
--------------------------------------------------------

ALTER TABLE blc_offer_rule_map add primary key (blc_offer_offer_id, map_key)
  USING INDEX  ENABLE
/
ALTER TABLE blc_offer_rule_map modify (map_key NOT NULL ENABLE)
/
ALTER TABLE blc_offer_rule_map modify (offer_rule_id NOT NULL ENABLE)
/
ALTER TABLE blc_offer_rule_map modify (blc_offer_offer_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_ORDER
--------------------------------------------------------

ALTER TABLE blc_order add primary key (order_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_order modify (customer_id NOT NULL ENABLE)
/
ALTER TABLE blc_order modify (order_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_ORDER_ADJUSTMENT
--------------------------------------------------------

ALTER TABLE blc_order_adjustment add primary key (order_adjustment_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_order_adjustment modify (offer_id NOT NULL ENABLE)
/
ALTER TABLE blc_order_adjustment modify (adjustment_value NOT NULL ENABLE)
/
ALTER TABLE blc_order_adjustment modify (adjustment_reason NOT NULL ENABLE)
/
ALTER TABLE blc_order_adjustment modify (order_adjustment_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_ORDER_ATTRIBUTE
--------------------------------------------------------

ALTER TABLE blc_order_attribute add primary key (order_attribute_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_order_attribute modify (order_id NOT NULL ENABLE)
/
ALTER TABLE blc_order_attribute modify (name NOT NULL ENABLE)
/
ALTER TABLE blc_order_attribute modify (order_attribute_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_ORDER_ITEM
--------------------------------------------------------

ALTER TABLE blc_order_item add primary key (order_item_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_order_item modify (quantity NOT NULL ENABLE)
/
ALTER TABLE blc_order_item modify (order_item_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_ORDER_ITEM_ADD_ATTR
--------------------------------------------------------

ALTER TABLE blc_order_item_add_attr add primary key (order_item_id, name)
  USING INDEX  ENABLE
/
ALTER TABLE blc_order_item_add_attr modify (name NOT NULL ENABLE)
/
ALTER TABLE blc_order_item_add_attr modify (order_item_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_ORDER_ITEM_ADJUSTMENT
--------------------------------------------------------

ALTER TABLE blc_order_item_adjustment add primary key (order_item_adjustment_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_order_item_adjustment modify (offer_id NOT NULL ENABLE)
/
ALTER TABLE blc_order_item_adjustment modify (adjustment_value NOT NULL ENABLE)
/
ALTER TABLE blc_order_item_adjustment modify (adjustment_reason NOT NULL ENABLE)
/
ALTER TABLE blc_order_item_adjustment modify (order_item_adjustment_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_ORDER_ITEM_ATTRIBUTE
--------------------------------------------------------

ALTER TABLE blc_order_item_attribute add primary key (order_item_attribute_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_order_item_attribute modify (order_item_id NOT NULL ENABLE)
/
ALTER TABLE blc_order_item_attribute modify (value NOT NULL ENABLE)
/
ALTER TABLE blc_order_item_attribute modify (name NOT NULL ENABLE)
/
ALTER TABLE blc_order_item_attribute modify (order_item_attribute_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_ORDER_ITEM_DTL_ADJ
--------------------------------------------------------

ALTER TABLE blc_order_item_dtl_adj add primary key (order_item_dtl_adj_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_order_item_dtl_adj modify (offer_id NOT NULL ENABLE)
/
ALTER TABLE blc_order_item_dtl_adj modify (adjustment_value NOT NULL ENABLE)
/
ALTER TABLE blc_order_item_dtl_adj modify (adjustment_reason NOT NULL ENABLE)
/
ALTER TABLE blc_order_item_dtl_adj modify (order_item_dtl_adj_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_ORDER_ITEM_PRICE_DTL
--------------------------------------------------------

ALTER TABLE blc_order_item_price_dtl add primary key (order_item_price_dtl_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_order_item_price_dtl modify (quantity NOT NULL ENABLE)
/
ALTER TABLE blc_order_item_price_dtl modify (order_item_price_dtl_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_ORDER_MULTISHIP_OPTION
--------------------------------------------------------

ALTER TABLE blc_order_multiship_option add primary key (order_multiship_option_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_order_multiship_option modify (order_multiship_option_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_ORDER_OFFER_CODE_XREF
--------------------------------------------------------

ALTER TABLE blc_order_offer_code_xref modify (offer_code_id NOT NULL ENABLE)
/
ALTER TABLE blc_order_offer_code_xref modify (order_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_ORDER_PAYMENT
--------------------------------------------------------

ALTER TABLE blc_order_payment add primary key (payment_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_order_payment modify (order_id NOT NULL ENABLE)
/
ALTER TABLE blc_order_payment modify (payment_type NOT NULL ENABLE)
/
ALTER TABLE blc_order_payment modify (payment_id NOT NULL ENABLE)
/
ALTER TABLE blc_order_payment add constraint uk9517a14fd790debd unique (reference_number)
  USING INDEX  ENABLE
/
--------------------------------------------------------
--  Constraints for Table BLC_ORDER_PAYMENT_DETAILS
--------------------------------------------------------

ALTER TABLE blc_order_payment_details add primary key (payment_detail_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_order_payment_details modify (payment_info NOT NULL ENABLE)
/
ALTER TABLE blc_order_payment_details modify (payment_detail_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_PAGE
--------------------------------------------------------

ALTER TABLE blc_page add primary key (page_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_page modify (page_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_PAGE_FLD
--------------------------------------------------------

ALTER TABLE blc_page_fld add primary key (page_fld_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_page_fld modify (page_fld_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_PAGE_FLD_MAP
--------------------------------------------------------

ALTER TABLE blc_page_fld_map add primary key (page_id, map_key)
  USING INDEX  ENABLE
/
ALTER TABLE blc_page_fld_map modify (map_key NOT NULL ENABLE)
/
ALTER TABLE blc_page_fld_map modify (page_fld_id NOT NULL ENABLE)
/
ALTER TABLE blc_page_fld_map modify (page_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_PAGE_ITEM_CRITERIA
--------------------------------------------------------

ALTER TABLE blc_page_item_criteria add primary key (page_item_criteria_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_page_item_criteria modify (quantity NOT NULL ENABLE)
/
ALTER TABLE blc_page_item_criteria modify (page_item_criteria_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_PAGE_RULE
--------------------------------------------------------

ALTER TABLE blc_page_rule add primary key (page_rule_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_page_rule modify (page_rule_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_PAGE_RULE_MAP
--------------------------------------------------------

ALTER TABLE blc_page_rule_map add primary key (blc_page_page_id, map_key)
  USING INDEX  ENABLE
/
ALTER TABLE blc_page_rule_map modify (map_key NOT NULL ENABLE)
/
ALTER TABLE blc_page_rule_map modify (page_rule_id NOT NULL ENABLE)
/
ALTER TABLE blc_page_rule_map modify (blc_page_page_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_PAGE_TMPLT
--------------------------------------------------------

ALTER TABLE blc_page_tmplt add primary key (page_tmplt_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_page_tmplt modify (page_tmplt_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_PAYINFO_ADDITIONAL_FIELDS
--------------------------------------------------------

ALTER TABLE blc_payinfo_additional_fields add primary key (payment_id, field_name)
  USING INDEX  ENABLE
/
ALTER TABLE blc_payinfo_additional_fields modify (field_name NOT NULL ENABLE)
/
ALTER TABLE blc_payinfo_additional_fields modify (payment_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_PAYMENT_ADDITIONAL_FIELDS
--------------------------------------------------------

ALTER TABLE blc_payment_additional_fields add primary key (payment_response_item_id, field_name)
  USING INDEX  ENABLE
/
ALTER TABLE blc_payment_additional_fields modify (field_name NOT NULL ENABLE)
/
ALTER TABLE blc_payment_additional_fields modify (payment_response_item_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_PAYMENT_LOG
--------------------------------------------------------

ALTER TABLE blc_payment_log add primary key (payment_log_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_payment_log modify (user_name NOT NULL ENABLE)
/
ALTER TABLE blc_payment_log modify (transaction_type NOT NULL ENABLE)
/
ALTER TABLE blc_payment_log modify (transaction_timestamp NOT NULL ENABLE)
/
ALTER TABLE blc_payment_log modify (log_type NOT NULL ENABLE)
/
ALTER TABLE blc_payment_log modify (payment_log_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_PAYMENT_RESPONSE_ITEM
--------------------------------------------------------

ALTER TABLE blc_payment_response_item add primary key (payment_response_item_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_payment_response_item modify (user_name NOT NULL ENABLE)
/
ALTER TABLE blc_payment_response_item modify (transaction_type NOT NULL ENABLE)
/
ALTER TABLE blc_payment_response_item modify (transaction_timestamp NOT NULL ENABLE)
/
ALTER TABLE blc_payment_response_item modify (payment_response_item_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_PERSONAL_MESSAGE
--------------------------------------------------------

ALTER TABLE blc_personal_message add primary key (personal_message_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_personal_message modify (personal_message_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_PGTMPLT_FLDGRP_XREF
--------------------------------------------------------

ALTER TABLE blc_pgtmplt_fldgrp_xref add primary key (page_tmplt_id, group_order)
  USING INDEX  ENABLE
/
ALTER TABLE blc_pgtmplt_fldgrp_xref modify (group_order NOT NULL ENABLE)
/
ALTER TABLE blc_pgtmplt_fldgrp_xref modify (fld_group_id NOT NULL ENABLE)
/
ALTER TABLE blc_pgtmplt_fldgrp_xref modify (page_tmplt_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_PHONE
--------------------------------------------------------

ALTER TABLE blc_phone add primary key (phone_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_phone modify (phone_number NOT NULL ENABLE)
/
ALTER TABLE blc_phone modify (phone_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_PRODUCT
--------------------------------------------------------

ALTER TABLE blc_product add primary key (product_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_product modify (is_featured_product NOT NULL ENABLE)
/
ALTER TABLE blc_product modify (product_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_PRODUCT_ATTRIBUTE
--------------------------------------------------------

ALTER TABLE blc_product_attribute add primary key (product_attribute_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_product_attribute modify (product_id NOT NULL ENABLE)
/
ALTER TABLE blc_product_attribute modify (name NOT NULL ENABLE)
/
ALTER TABLE blc_product_attribute modify (product_attribute_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_PRODUCT_BUNDLE
--------------------------------------------------------

ALTER TABLE blc_product_bundle add primary key (product_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_product_bundle modify (product_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_PRODUCT_CROSS_SALE
--------------------------------------------------------

ALTER TABLE blc_product_cross_sale modify (related_sale_product_id NOT NULL ENABLE)
/
ALTER TABLE blc_product_cross_sale modify (cross_sale_product_id NOT NULL ENABLE)
/
ALTER TABLE blc_product_cross_sale add primary key (cross_sale_product_id)
  USING INDEX  ENABLE
/
--------------------------------------------------------
--  Constraints for Table BLC_PRODUCT_FEATURED
--------------------------------------------------------

ALTER TABLE blc_product_featured add primary key (featured_product_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_product_featured modify (featured_product_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_PRODUCT_OPTION
--------------------------------------------------------

ALTER TABLE blc_product_option add primary key (product_option_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_product_option modify (product_option_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_PRODUCT_OPTION_VALUE
--------------------------------------------------------

ALTER TABLE blc_product_option_value add primary key (product_option_value_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_product_option_value modify (product_option_value_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_PRODUCT_OPTION_XREF
--------------------------------------------------------

ALTER TABLE blc_product_option_xref modify (product_id NOT NULL ENABLE)
/
ALTER TABLE blc_product_option_xref modify (product_option_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_PRODUCT_SKU_XREF
--------------------------------------------------------

ALTER TABLE blc_product_sku_xref add primary key (sku_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_product_sku_xref modify (sku_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_PRODUCT_UP_SALE
--------------------------------------------------------

ALTER TABLE blc_product_up_sale add primary key (up_sale_product_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_product_up_sale modify (up_sale_product_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_QUAL_CRIT_OFFER_XREF
--------------------------------------------------------

ALTER TABLE blc_qual_crit_offer_xref add primary key (offer_id, offer_item_criteria_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_qual_crit_offer_xref modify (offer_item_criteria_id NOT NULL ENABLE)
/
ALTER TABLE blc_qual_crit_offer_xref modify (offer_id NOT NULL ENABLE)
/
ALTER TABLE blc_qual_crit_offer_xref add constraint ukd592e919e7ab0252 unique (offer_item_criteria_id)
  USING INDEX  ENABLE
/
--------------------------------------------------------
--  Constraints for Table BLC_QUAL_CRIT_PAGE_XREF
--------------------------------------------------------

ALTER TABLE blc_qual_crit_page_xref add primary key (page_id, page_item_criteria_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_qual_crit_page_xref modify (page_item_criteria_id NOT NULL ENABLE)
/
ALTER TABLE blc_qual_crit_page_xref add constraint uk874be5902b6bc67f unique (page_item_criteria_id)
  USING INDEX  ENABLE
/
--------------------------------------------------------
--  Constraints for Table BLC_QUAL_CRIT_SC_XREF
--------------------------------------------------------

ALTER TABLE blc_qual_crit_sc_xref add primary key (sc_id, sc_item_criteria_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_qual_crit_sc_xref modify (sc_item_criteria_id NOT NULL ENABLE)
/
ALTER TABLE blc_qual_crit_sc_xref modify (sc_id NOT NULL ENABLE)
/
ALTER TABLE blc_qual_crit_sc_xref add constraint ukc4a353afff06f4de unique (sc_item_criteria_id)
  USING INDEX  ENABLE
/
--------------------------------------------------------
--  Constraints for Table BLC_RATING_DETAIL
--------------------------------------------------------

ALTER TABLE blc_rating_detail add primary key (rating_detail_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_rating_detail modify (rating_summary_id NOT NULL ENABLE)
/
ALTER TABLE blc_rating_detail modify (customer_id NOT NULL ENABLE)
/
ALTER TABLE blc_rating_detail modify (rating_submitted_date NOT NULL ENABLE)
/
ALTER TABLE blc_rating_detail modify (rating NOT NULL ENABLE)
/
ALTER TABLE blc_rating_detail modify (rating_detail_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_RATING_SUMMARY
--------------------------------------------------------

ALTER TABLE blc_rating_summary add primary key (rating_summary_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_rating_summary modify (rating_type NOT NULL ENABLE)
/
ALTER TABLE blc_rating_summary modify (item_id NOT NULL ENABLE)
/
ALTER TABLE blc_rating_summary modify (average_rating NOT NULL ENABLE)
/
ALTER TABLE blc_rating_summary modify (rating_summary_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_REVIEW_DETAIL
--------------------------------------------------------

ALTER TABLE blc_review_detail add primary key (review_detail_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_review_detail modify (rating_summary_id NOT NULL ENABLE)
/
ALTER TABLE blc_review_detail modify (customer_id NOT NULL ENABLE)
/
ALTER TABLE blc_review_detail modify (review_text NOT NULL ENABLE)
/
ALTER TABLE blc_review_detail modify (review_status NOT NULL ENABLE)
/
ALTER TABLE blc_review_detail modify (review_submitted_date NOT NULL ENABLE)
/
ALTER TABLE blc_review_detail modify (not_helpful_count NOT NULL ENABLE)
/
ALTER TABLE blc_review_detail modify (helpful_count NOT NULL ENABLE)
/
ALTER TABLE blc_review_detail modify (review_detail_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_REVIEW_FEEDBACK
--------------------------------------------------------

ALTER TABLE blc_review_feedback add primary key (review_feedback_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_review_feedback modify (review_detail_id NOT NULL ENABLE)
/
ALTER TABLE blc_review_feedback modify (customer_id NOT NULL ENABLE)
/
ALTER TABLE blc_review_feedback modify (is_helpful NOT NULL ENABLE)
/
ALTER TABLE blc_review_feedback modify (review_feedback_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_ROLE
--------------------------------------------------------

ALTER TABLE blc_role add primary key (role_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_role modify (role_name NOT NULL ENABLE)
/
ALTER TABLE blc_role modify (role_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_SANDBOX
--------------------------------------------------------

ALTER TABLE blc_sandbox add primary key (sandbox_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_sandbox modify (sandbox_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_SANDBOX_ACTION
--------------------------------------------------------

ALTER TABLE blc_sandbox_action add primary key (sandbox_action_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_sandbox_action modify (sandbox_action_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_SANDBOX_ITEM
--------------------------------------------------------

ALTER TABLE blc_sandbox_item add primary key (sandbox_item_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_sandbox_item modify (sandbox_item_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_SC
--------------------------------------------------------

ALTER TABLE blc_sc add primary key (sc_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_sc modify (locale_code NOT NULL ENABLE)
/
ALTER TABLE blc_sc modify (priority NOT NULL ENABLE)
/
ALTER TABLE blc_sc modify (content_name NOT NULL ENABLE)
/
ALTER TABLE blc_sc modify (sc_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_SC_FLD
--------------------------------------------------------

ALTER TABLE blc_sc_fld add primary key (sc_fld_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_sc_fld modify (sc_fld_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_SC_FLDGRP_XREF
--------------------------------------------------------

ALTER TABLE blc_sc_fldgrp_xref add primary key (sc_fld_tmplt_id, group_order)
  USING INDEX  ENABLE
/
ALTER TABLE blc_sc_fldgrp_xref modify (group_order NOT NULL ENABLE)
/
ALTER TABLE blc_sc_fldgrp_xref modify (fld_group_id NOT NULL ENABLE)
/
ALTER TABLE blc_sc_fldgrp_xref modify (sc_fld_tmplt_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_SC_FLD_MAP
--------------------------------------------------------

ALTER TABLE blc_sc_fld_map add primary key (sc_id, map_key)
  USING INDEX  ENABLE
/
ALTER TABLE blc_sc_fld_map modify (map_key NOT NULL ENABLE)
/
ALTER TABLE blc_sc_fld_map modify (sc_fld_id NOT NULL ENABLE)
/
ALTER TABLE blc_sc_fld_map modify (sc_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_SC_FLD_TMPLT
--------------------------------------------------------

ALTER TABLE blc_sc_fld_tmplt add primary key (sc_fld_tmplt_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_sc_fld_tmplt modify (sc_fld_tmplt_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_SC_ITEM_CRITERIA
--------------------------------------------------------

ALTER TABLE blc_sc_item_criteria add primary key (sc_item_criteria_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_sc_item_criteria modify (quantity NOT NULL ENABLE)
/
ALTER TABLE blc_sc_item_criteria modify (sc_item_criteria_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_SC_RULE
--------------------------------------------------------

ALTER TABLE blc_sc_rule modify (sc_rule_id NOT NULL ENABLE)
/
ALTER TABLE blc_sc_rule add primary key (sc_rule_id)
  USING INDEX  ENABLE
/
--------------------------------------------------------
--  Constraints for Table BLC_SC_RULE_MAP
--------------------------------------------------------

ALTER TABLE blc_sc_rule_map add primary key (blc_sc_sc_id, map_key)
  USING INDEX  ENABLE
/
ALTER TABLE blc_sc_rule_map modify (map_key NOT NULL ENABLE)
/
ALTER TABLE blc_sc_rule_map modify (sc_rule_id NOT NULL ENABLE)
/
ALTER TABLE blc_sc_rule_map modify (blc_sc_sc_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_SC_TYPE
--------------------------------------------------------

ALTER TABLE blc_sc_type add primary key (sc_type_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_sc_type modify (sc_type_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_SEARCH_FACET
--------------------------------------------------------

ALTER TABLE blc_search_facet add primary key (search_facet_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_search_facet modify (field_id NOT NULL ENABLE)
/
ALTER TABLE blc_search_facet modify (search_facet_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_SEARCH_FACET_RANGE
--------------------------------------------------------

ALTER TABLE blc_search_facet_range add primary key (search_facet_range_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_search_facet_range modify (min_value NOT NULL ENABLE)
/
ALTER TABLE blc_search_facet_range modify (search_facet_range_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_SEARCH_FACET_XREF
--------------------------------------------------------

ALTER TABLE blc_search_facet_xref add primary key (id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_search_facet_xref modify (required_facet_id NOT NULL ENABLE)
/
ALTER TABLE blc_search_facet_xref modify (id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_SEARCH_INTERCEPT
--------------------------------------------------------

ALTER TABLE blc_search_intercept add primary key (search_redirect_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_search_intercept modify (url NOT NULL ENABLE)
/
ALTER TABLE blc_search_intercept modify (search_term NOT NULL ENABLE)
/
ALTER TABLE blc_search_intercept modify (search_redirect_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_SEARCH_SYNONYM
--------------------------------------------------------

ALTER TABLE blc_search_synonym add primary key (search_synonym_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_search_synonym modify (search_synonym_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_SHIPPING_RATE
--------------------------------------------------------

ALTER TABLE blc_shipping_rate add primary key (id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_shipping_rate modify (fee_type NOT NULL ENABLE)
/
ALTER TABLE blc_shipping_rate modify (fee_band NOT NULL ENABLE)
/
ALTER TABLE blc_shipping_rate modify (band_unit_qty NOT NULL ENABLE)
/
ALTER TABLE blc_shipping_rate modify (band_result_qty NOT NULL ENABLE)
/
ALTER TABLE blc_shipping_rate modify (band_result_pct NOT NULL ENABLE)
/
ALTER TABLE blc_shipping_rate modify (id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_SITE
--------------------------------------------------------

ALTER TABLE blc_site add primary key (site_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_site modify (site_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_SITE_CATALOG
--------------------------------------------------------

ALTER TABLE blc_site_catalog modify (site_id NOT NULL ENABLE)
/
ALTER TABLE blc_site_catalog modify (catalog_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_SITE_SANDBOX
--------------------------------------------------------

ALTER TABLE blc_site_sandbox add primary key (sandbox_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_site_sandbox modify (sandbox_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_SKU
--------------------------------------------------------

ALTER TABLE blc_sku add primary key (sku_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_sku modify (sku_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_SKU_ATTRIBUTE
--------------------------------------------------------

ALTER TABLE blc_sku_attribute add primary key (sku_attr_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_sku_attribute modify (sku_id NOT NULL ENABLE)
/
ALTER TABLE blc_sku_attribute modify (value NOT NULL ENABLE)
/
ALTER TABLE blc_sku_attribute modify (name NOT NULL ENABLE)
/
ALTER TABLE blc_sku_attribute modify (sku_attr_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_SKU_AVAILABILITY
--------------------------------------------------------

ALTER TABLE blc_sku_availability add primary key (sku_availability_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_sku_availability modify (sku_availability_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_SKU_BUNDLE_ITEM
--------------------------------------------------------

ALTER TABLE blc_sku_bundle_item add primary key (sku_bundle_item_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_sku_bundle_item modify (sku_id NOT NULL ENABLE)
/
ALTER TABLE blc_sku_bundle_item modify (product_bundle_id NOT NULL ENABLE)
/
ALTER TABLE blc_sku_bundle_item modify (quantity NOT NULL ENABLE)
/
ALTER TABLE blc_sku_bundle_item modify (sku_bundle_item_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_SKU_FEE
--------------------------------------------------------

ALTER TABLE blc_sku_fee add primary key (sku_fee_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_sku_fee modify (amount NOT NULL ENABLE)
/
ALTER TABLE blc_sku_fee modify (sku_fee_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_SKU_FEE_XREF
--------------------------------------------------------

ALTER TABLE blc_sku_fee_xref modify (sku_id NOT NULL ENABLE)
/
ALTER TABLE blc_sku_fee_xref modify (sku_fee_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_SKU_FULFILLMENT_EXCLUDED
--------------------------------------------------------

ALTER TABLE blc_sku_fulfillment_excluded modify (fulfillment_option_id NOT NULL ENABLE)
/
ALTER TABLE blc_sku_fulfillment_excluded modify (sku_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_SKU_FULFILLMENT_FLAT_RATES
--------------------------------------------------------

ALTER TABLE blc_sku_fulfillment_flat_rates add primary key (sku_id, fulfillment_option_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_sku_fulfillment_flat_rates modify (fulfillment_option_id NOT NULL ENABLE)
/
ALTER TABLE blc_sku_fulfillment_flat_rates modify (sku_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_SKU_MEDIA_MAP
--------------------------------------------------------

ALTER TABLE blc_sku_media_map add primary key (blc_sku_sku_id, map_key)
  USING INDEX  ENABLE
/
ALTER TABLE blc_sku_media_map modify (map_key NOT NULL ENABLE)
/
ALTER TABLE blc_sku_media_map modify (media_id NOT NULL ENABLE)
/
ALTER TABLE blc_sku_media_map modify (blc_sku_sku_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_SKU_OPTION_VALUE_XREF
--------------------------------------------------------

ALTER TABLE blc_sku_option_value_xref modify (product_option_value_id NOT NULL ENABLE)
/
ALTER TABLE blc_sku_option_value_xref modify (sku_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_STATE
--------------------------------------------------------

ALTER TABLE blc_state add primary key (abbreviation)
  USING INDEX  ENABLE
/
ALTER TABLE blc_state modify (country NOT NULL ENABLE)
/
ALTER TABLE blc_state modify (name NOT NULL ENABLE)
/
ALTER TABLE blc_state modify (abbreviation NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_STATIC_ASSET
--------------------------------------------------------

ALTER TABLE blc_static_asset add primary key (static_asset_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_static_asset modify (name NOT NULL ENABLE)
/
ALTER TABLE blc_static_asset modify (full_url NOT NULL ENABLE)
/
ALTER TABLE blc_static_asset modify (static_asset_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_STATIC_ASSET_DESC
--------------------------------------------------------

ALTER TABLE blc_static_asset_desc add primary key (static_asset_desc_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_static_asset_desc modify (static_asset_desc_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_STATIC_ASSET_STRG
--------------------------------------------------------

ALTER TABLE blc_static_asset_strg add primary key (static_asset_strg_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_static_asset_strg modify (static_asset_id NOT NULL ENABLE)
/
ALTER TABLE blc_static_asset_strg modify (static_asset_strg_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_STORE
--------------------------------------------------------

ALTER TABLE blc_store add primary key (store_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_store modify (store_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_SYSTEM_PROPERTY
--------------------------------------------------------

ALTER TABLE blc_system_property add constraint property_name_ unique (property_name)
  USING INDEX  ENABLE
/
ALTER TABLE blc_system_property add primary key (blc_system_property_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_system_property modify (property_value NOT NULL ENABLE)
/
ALTER TABLE blc_system_property modify (property_name NOT NULL ENABLE)
/
ALTER TABLE blc_system_property modify (blc_system_property_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_TAR_CRIT_OFFER_XREF
--------------------------------------------------------

ALTER TABLE blc_tar_crit_offer_xref add constraint uk125f5803e7ab0252 unique (offer_item_criteria_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_tar_crit_offer_xref add primary key (offer_id, offer_item_criteria_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_tar_crit_offer_xref modify (offer_item_criteria_id NOT NULL ENABLE)
/
ALTER TABLE blc_tar_crit_offer_xref modify (offer_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_TAX_DETAIL
--------------------------------------------------------

ALTER TABLE blc_tax_detail add primary key (tax_detail_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_tax_detail modify (tax_detail_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_TRANSLATION
--------------------------------------------------------

ALTER TABLE blc_translation add primary key (translation_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_translation modify (translation_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_URL_HANDLER
--------------------------------------------------------

ALTER TABLE blc_url_handler add primary key (url_handler_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_url_handler modify (new_url NOT NULL ENABLE)
/
ALTER TABLE blc_url_handler modify (incoming_url NOT NULL ENABLE)
/
ALTER TABLE blc_url_handler modify (url_handler_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_USERCONNECTION
--------------------------------------------------------

ALTER TABLE blc_userconnection add primary key (providerid, provideruserid, userid)
  USING INDEX  ENABLE
/
ALTER TABLE blc_userconnection modify (rank NOT NULL ENABLE)
/
ALTER TABLE blc_userconnection modify (accesstoken NOT NULL ENABLE)
/
ALTER TABLE blc_userconnection modify (userid NOT NULL ENABLE)
/
ALTER TABLE blc_userconnection modify (provideruserid NOT NULL ENABLE)
/
ALTER TABLE blc_userconnection modify (providerid NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table BLC_ZIP_CODE
--------------------------------------------------------

ALTER TABLE blc_zip_code add primary key (zip_code_id)
  USING INDEX  ENABLE
/
ALTER TABLE blc_zip_code modify (zip_code_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table SANDBOX_ITEM_ACTION
--------------------------------------------------------

ALTER TABLE sandbox_item_action modify (sandbox_item_id NOT NULL ENABLE)
/
ALTER TABLE sandbox_item_action modify (sandbox_action_id NOT NULL ENABLE)
/
--------------------------------------------------------
--  Constraints for Table SEQUENCE_GENERATOR
--------------------------------------------------------

ALTER TABLE sequence_generator add primary key (id_name)
  USING INDEX  ENABLE
/
ALTER TABLE sequence_generator modify (id_name NOT NULL ENABLE)
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_ADDITIONAL_OFFER_INFO
--------------------------------------------------------

ALTER TABLE blc_additional_offer_info add constraint fk3bfdbd631891ff79 foreign key (blc_order_order_id)
  REFERENCES blc_order (order_id) ENABLE
/
ALTER TABLE blc_additional_offer_info add constraint fk3bfdbd63b5d9c34d foreign key (offer_info_id)
  REFERENCES blc_offer_info (offer_info_id) ENABLE
/
ALTER TABLE blc_additional_offer_info add constraint fk3bfdbd63d5f3faf4 foreign key (offer_id)
  REFERENCES blc_offer (offer_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_ADDRESS
--------------------------------------------------------

ALTER TABLE blc_address add constraint fk299f86ce337c4d50 foreign key (state_prov_region)
  REFERENCES blc_state (abbreviation) ENABLE
/
ALTER TABLE blc_address add constraint fk299f86cea46e16cf foreign key (country)
  REFERENCES blc_country (abbreviation) ENABLE
/
ALTER TABLE blc_address add constraint fk299f86cebf4449ba foreign key (phone_primary_id)
  REFERENCES blc_phone (phone_id) ENABLE
/
ALTER TABLE blc_address add constraint fk299f86cee12dc0c8 foreign key (phone_secondary_id)
  REFERENCES blc_phone (phone_id) ENABLE
/
ALTER TABLE blc_address add constraint fk299f86cef1a6533f foreign key (phone_fax_id)
  REFERENCES blc_phone (phone_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_ADMIN_PERMISSION_ENTITY
--------------------------------------------------------

ALTER TABLE blc_admin_permission_entity add constraint fk23c09e3de88b7d38 foreign key (admin_permission_id)
  REFERENCES blc_admin_permission (admin_permission_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_ADMIN_ROLE_PERMISSION_XREF
--------------------------------------------------------

ALTER TABLE blc_admin_role_permission_xref add constraint fk4a819d985f43aad8 foreign key (admin_role_id)
  REFERENCES blc_admin_role (admin_role_id) ENABLE
/
ALTER TABLE blc_admin_role_permission_xref add constraint fk4a819d98e88b7d38 foreign key (admin_permission_id)
  REFERENCES blc_admin_permission (admin_permission_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_ADMIN_SECTION
--------------------------------------------------------

ALTER TABLE blc_admin_section add constraint fk7ea7d92fb1a18498 foreign key (admin_module_id)
  REFERENCES blc_admin_module (admin_module_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_ADMIN_SEC_PERM_XREF
--------------------------------------------------------

ALTER TABLE blc_admin_sec_perm_xref add constraint fk5e8329663af7f0fc foreign key (admin_section_id)
  REFERENCES blc_admin_section (admin_section_id) ENABLE
/
ALTER TABLE blc_admin_sec_perm_xref add constraint fk5e832966e88b7d38 foreign key (admin_permission_id)
  REFERENCES blc_admin_permission (admin_permission_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_ADMIN_USER_PERMISSION_XREF
--------------------------------------------------------

ALTER TABLE blc_admin_user_permission_xref add constraint fkf0b3beed46ebc38 foreign key (admin_user_id)
  REFERENCES blc_admin_user (admin_user_id) ENABLE
/
ALTER TABLE blc_admin_user_permission_xref add constraint fkf0b3beede88b7d38 foreign key (admin_permission_id)
  REFERENCES blc_admin_permission (admin_permission_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_ADMIN_USER_ROLE_XREF
--------------------------------------------------------

ALTER TABLE blc_admin_user_role_xref add constraint fkffd33a2646ebc38 foreign key (admin_user_id)
  REFERENCES blc_admin_user (admin_user_id) ENABLE
/
ALTER TABLE blc_admin_user_role_xref add constraint fkffd33a265f43aad8 foreign key (admin_role_id)
  REFERENCES blc_admin_role (admin_role_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_ADMIN_USER_SANDBOX
--------------------------------------------------------

ALTER TABLE blc_admin_user_sandbox add constraint fkd0a97e0946ebc38 foreign key (admin_user_id)
  REFERENCES blc_admin_user (admin_user_id) ENABLE
/
ALTER TABLE blc_admin_user_sandbox add constraint fkd0a97e09579fe59d foreign key (sandbox_id)
  REFERENCES blc_sandbox (sandbox_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_AMOUNT_ITEM
--------------------------------------------------------

ALTER TABLE blc_amount_item add constraint fkb98530944bc71d98 foreign key (payment_id)
  REFERENCES blc_order_payment (payment_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_ASSET_DESC_MAP
--------------------------------------------------------

ALTER TABLE blc_asset_desc_map add constraint fke886bae367f70b63 foreign key (static_asset_id)
  REFERENCES blc_static_asset (static_asset_id) ENABLE
/
ALTER TABLE blc_asset_desc_map add constraint fke886bae3e2ba0c9d foreign key (static_asset_desc_id)
  REFERENCES blc_static_asset_desc (static_asset_desc_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_BUNDLE_ORDER_ITEM
--------------------------------------------------------

ALTER TABLE blc_bundle_order_item add constraint fk489703db9af166df foreign key (order_item_id)
  REFERENCES blc_order_item (order_item_id) ENABLE
/
ALTER TABLE blc_bundle_order_item add constraint fk489703dbb78c9977 foreign key (sku_id)
  REFERENCES blc_sku (sku_id) ENABLE
/
ALTER TABLE blc_bundle_order_item add constraint fk489703dbccf29b96 foreign key (product_bundle_id)
  REFERENCES blc_product_bundle (product_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_BUND_ITEM_FEE_PRICE
--------------------------------------------------------

ALTER TABLE blc_bund_item_fee_price add constraint fk14267a943fc68307 foreign key (bund_order_item_id)
  REFERENCES blc_bundle_order_item (order_item_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_CANDIDATE_FG_OFFER
--------------------------------------------------------

ALTER TABLE blc_candidate_fg_offer add constraint fkce785605028dc55 foreign key (fulfillment_group_id)
  REFERENCES blc_fulfillment_group (fulfillment_group_id) ENABLE
/
ALTER TABLE blc_candidate_fg_offer add constraint fkce78560d5f3faf4 foreign key (offer_id)
  REFERENCES blc_offer (offer_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_CANDIDATE_ITEM_OFFER
--------------------------------------------------------

ALTER TABLE blc_candidate_item_offer add constraint fk9eee9b29af166df foreign key (order_item_id)
  REFERENCES blc_order_item (order_item_id) ENABLE
/
ALTER TABLE blc_candidate_item_offer add constraint fk9eee9b2d5f3faf4 foreign key (offer_id)
  REFERENCES blc_offer (offer_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_CANDIDATE_ORDER_OFFER
--------------------------------------------------------

ALTER TABLE blc_candidate_order_offer add constraint fk6185228989fe8a02 foreign key (order_id)
  REFERENCES blc_order (order_id) ENABLE
/
ALTER TABLE blc_candidate_order_offer add constraint fk61852289d5f3faf4 foreign key (offer_id)
  REFERENCES blc_offer (offer_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_CATEGORY
--------------------------------------------------------

ALTER TABLE blc_category add constraint fk55f82d44b177e6 foreign key (default_parent_category_id)
  REFERENCES blc_category (category_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_CATEGORY_ATTRIBUTE
--------------------------------------------------------

ALTER TABLE blc_category_attribute add constraint fk4e441d4115d1a13d foreign key (category_id)
  REFERENCES blc_category (category_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_CATEGORY_IMAGE
--------------------------------------------------------

ALTER TABLE blc_category_image add constraint fk27cf3e8015d1a13d foreign key (category_id)
  REFERENCES blc_category (category_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_CATEGORY_MEDIA_MAP
--------------------------------------------------------

ALTER TABLE blc_category_media_map add constraint fkcd24b1066e4720e0 foreign key (media_id)
  REFERENCES blc_media (media_id) ENABLE
/
ALTER TABLE blc_category_media_map add constraint fkcd24b106d786cea2 foreign key (blc_category_category_id)
  REFERENCES blc_category (category_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_CATEGORY_PRODUCT_XREF
--------------------------------------------------------

ALTER TABLE blc_category_product_xref add constraint fk635eb1a615d1a13d foreign key (category_id)
  REFERENCES blc_category (category_id) ENABLE
/
ALTER TABLE blc_category_product_xref add constraint fk635eb1a65f11a0b7 foreign key (product_id)
  REFERENCES blc_product (product_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_CATEGORY_XREF
--------------------------------------------------------

ALTER TABLE blc_category_xref add constraint fke889733615d1a13d foreign key (category_id)
  REFERENCES blc_category (category_id) ENABLE
/
ALTER TABLE blc_category_xref add constraint fke8897336d6d45dbe foreign key (sub_category_id)
  REFERENCES blc_category (category_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_CAT_SEARCH_FACET_EXCL_XREF
--------------------------------------------------------

ALTER TABLE blc_cat_search_facet_excl_xref add constraint fk8361ef4e15d1a13d foreign key (category_id)
  REFERENCES blc_category (category_id) ENABLE
/
ALTER TABLE blc_cat_search_facet_excl_xref add constraint fk8361ef4eb96b1c93 foreign key (search_facet_id)
  REFERENCES blc_search_facet (search_facet_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_CAT_SEARCH_FACET_XREF
--------------------------------------------------------

ALTER TABLE blc_cat_search_facet_xref add constraint fk32210eeb15d1a13d foreign key (category_id)
  REFERENCES blc_category (category_id) ENABLE
/
ALTER TABLE blc_cat_search_facet_xref add constraint fk32210eebb96b1c93 foreign key (search_facet_id)
  REFERENCES blc_search_facet (search_facet_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_CUSTOMER
--------------------------------------------------------

ALTER TABLE blc_customer add constraint fk7716f0241422b204 foreign key (challenge_question_id)
  REFERENCES blc_challenge_question (question_id) ENABLE
/
ALTER TABLE blc_customer add constraint fk7716f024a1e1c128 foreign key (locale_code)
  REFERENCES blc_locale (locale_code) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_CUSTOMER_ADDRESS
--------------------------------------------------------

ALTER TABLE blc_customer_address add constraint fk75b95ab97470f437 foreign key (customer_id)
  REFERENCES blc_customer (customer_id) ENABLE
/
ALTER TABLE blc_customer_address add constraint fk75b95ab9c13085dd foreign key (address_id)
  REFERENCES blc_address (address_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_CUSTOMER_ATTRIBUTE
--------------------------------------------------------

ALTER TABLE blc_customer_attribute add constraint fkb974c8217470f437 foreign key (customer_id)
  REFERENCES blc_customer (customer_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_CUSTOMER_OFFER_XREF
--------------------------------------------------------

ALTER TABLE blc_customer_offer_xref add constraint fk685e80397470f437 foreign key (customer_id)
  REFERENCES blc_customer (customer_id) ENABLE
/
ALTER TABLE blc_customer_offer_xref add constraint fk685e8039d5f3faf4 foreign key (offer_id)
  REFERENCES blc_offer (offer_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_CUSTOMER_PAYMENT
--------------------------------------------------------

ALTER TABLE blc_customer_payment add constraint fk8b3df0cb7470f437 foreign key (customer_id)
  REFERENCES blc_customer (customer_id) ENABLE
/
ALTER TABLE blc_customer_payment add constraint fk8b3df0cbc13085dd foreign key (address_id)
  REFERENCES blc_address (address_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_CUSTOMER_PAYMENT_FIELDS
--------------------------------------------------------

ALTER TABLE blc_customer_payment_fields add constraint fk5ccb14adca0b98e0 foreign key (customer_payment_id)
  REFERENCES blc_customer_payment (customer_payment_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_CUSTOMER_PHONE
--------------------------------------------------------

ALTER TABLE blc_customer_phone add constraint fk3d28ed737470f437 foreign key (customer_id)
  REFERENCES blc_customer (customer_id) ENABLE
/
ALTER TABLE blc_customer_phone add constraint fk3d28ed73d894cb5d foreign key (phone_id)
  REFERENCES blc_phone (phone_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_CUSTOMER_ROLE
--------------------------------------------------------

ALTER TABLE blc_customer_role add constraint fk548eb7b17470f437 foreign key (customer_id)
  REFERENCES blc_customer (customer_id) ENABLE
/
ALTER TABLE blc_customer_role add constraint fk548eb7b1b8587b7 foreign key (role_id)
  REFERENCES blc_role (role_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_DATA_DRVN_ENUM_VAL
--------------------------------------------------------

ALTER TABLE blc_data_drvn_enum_val add constraint fkb2d5700da60e0554 foreign key (enum_type)
  REFERENCES blc_data_drvn_enum (enum_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_DISCRETE_ORDER_ITEM
--------------------------------------------------------

ALTER TABLE blc_discrete_order_item add constraint fkbc3a8a841285903b foreign key (sku_bundle_item_id)
  REFERENCES blc_sku_bundle_item (sku_bundle_item_id) ENABLE
/
ALTER TABLE blc_discrete_order_item add constraint fkbc3a8a845cdfca80 foreign key (bundle_order_item_id)
  REFERENCES blc_bundle_order_item (order_item_id) ENABLE
/
ALTER TABLE blc_discrete_order_item add constraint fkbc3a8a845f11a0b7 foreign key (product_id)
  REFERENCES blc_product (product_id) ENABLE
/
ALTER TABLE blc_discrete_order_item add constraint fkbc3a8a849af166df foreign key (order_item_id)
  REFERENCES blc_order_item (order_item_id) ENABLE
/
ALTER TABLE blc_discrete_order_item add constraint fkbc3a8a84b78c9977 foreign key (sku_id)
  REFERENCES blc_sku (sku_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_DISC_ITEM_FEE_PRICE
--------------------------------------------------------

ALTER TABLE blc_disc_item_fee_price add constraint fk2a641cc8b76b9466 foreign key (order_item_id)
  REFERENCES blc_discrete_order_item (order_item_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_DYN_DISCRETE_ORDER_ITEM
--------------------------------------------------------

ALTER TABLE blc_dyn_discrete_order_item add constraint fk209dee9eb76b9466 foreign key (order_item_id)
  REFERENCES blc_discrete_order_item (order_item_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_EMAIL_TRACKING_CLICKS
--------------------------------------------------------

ALTER TABLE blc_email_tracking_clicks add constraint fkfdf9f52afa1e5d61 foreign key (email_tracking_id)
  REFERENCES blc_email_tracking (email_tracking_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_EMAIL_TRACKING_OPENS
--------------------------------------------------------

ALTER TABLE blc_email_tracking_opens add constraint fka5c3722afa1e5d61 foreign key (email_tracking_id)
  REFERENCES blc_email_tracking (email_tracking_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_FG_ADJUSTMENT
--------------------------------------------------------

ALTER TABLE blc_fg_adjustment add constraint fk468c8f255028dc55 foreign key (fulfillment_group_id)
  REFERENCES blc_fulfillment_group (fulfillment_group_id) ENABLE
/
ALTER TABLE blc_fg_adjustment add constraint fk468c8f25d5f3faf4 foreign key (offer_id)
  REFERENCES blc_offer (offer_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_FG_FEE_TAX_XREF
--------------------------------------------------------

ALTER TABLE blc_fg_fee_tax_xref add constraint fk25426dc0598f6d02 foreign key (fulfillment_group_fee_id)
  REFERENCES blc_fulfillment_group_fee (fulfillment_group_fee_id) ENABLE
/
ALTER TABLE blc_fg_fee_tax_xref add constraint fk25426dc071448c19 foreign key (tax_detail_id)
  REFERENCES blc_tax_detail (tax_detail_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_FG_FG_TAX_XREF
--------------------------------------------------------

ALTER TABLE blc_fg_fg_tax_xref add constraint fk61bea4555028dc55 foreign key (fulfillment_group_id)
  REFERENCES blc_fulfillment_group (fulfillment_group_id) ENABLE
/
ALTER TABLE blc_fg_fg_tax_xref add constraint fk61bea45571448c19 foreign key (tax_detail_id)
  REFERENCES blc_tax_detail (tax_detail_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_FG_ITEM_TAX_XREF
--------------------------------------------------------

ALTER TABLE blc_fg_item_tax_xref add constraint fkdd3e844371448c19 foreign key (tax_detail_id)
  REFERENCES blc_tax_detail (tax_detail_id) ENABLE
/
ALTER TABLE blc_fg_item_tax_xref add constraint fkdd3e8443e3bbb4d2 foreign key (fulfillment_group_item_id)
  REFERENCES blc_fulfillment_group_item (fulfillment_group_item_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_FIELD_SEARCH_TYPES
--------------------------------------------------------

ALTER TABLE blc_field_search_types add constraint fkf52d130d3c3907c4 foreign key (field_id)
  REFERENCES blc_field (field_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_FLD_DEF
--------------------------------------------------------

ALTER TABLE blc_fld_def add constraint fk3fcb575e6a79bdb5 foreign key (fld_group_id)
  REFERENCES blc_fld_group (fld_group_id) ENABLE
/
ALTER TABLE blc_fld_def add constraint fk3fcb575efd2ea299 foreign key (fld_enum_id)
  REFERENCES blc_fld_enum (fld_enum_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_FLD_ENUM_ITEM
--------------------------------------------------------

ALTER TABLE blc_fld_enum_item add constraint fk83a6a84afd2ea299 foreign key (fld_enum_id)
  REFERENCES blc_fld_enum (fld_enum_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_FULFILLMENT_GROUP
--------------------------------------------------------

ALTER TABLE blc_fulfillment_group add constraint fkc5b9ef1877f565e1 foreign key (personal_message_id)
  REFERENCES blc_personal_message (personal_message_id) ENABLE
/
ALTER TABLE blc_fulfillment_group add constraint fkc5b9ef1881f34c7f foreign key (fulfillment_option_id)
  REFERENCES blc_fulfillment_option (fulfillment_option_id) ENABLE
/
ALTER TABLE blc_fulfillment_group add constraint fkc5b9ef1889fe8a02 foreign key (order_id)
  REFERENCES blc_order (order_id) ENABLE
/
ALTER TABLE blc_fulfillment_group add constraint fkc5b9ef18c13085dd foreign key (address_id)
  REFERENCES blc_address (address_id) ENABLE
/
ALTER TABLE blc_fulfillment_group add constraint fkc5b9ef18d894cb5d foreign key (phone_id)
  REFERENCES blc_phone (phone_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_FULFILLMENT_GROUP_FEE
--------------------------------------------------------

ALTER TABLE blc_fulfillment_group_fee add constraint fk6aa8e1bf5028dc55 foreign key (fulfillment_group_id)
  REFERENCES blc_fulfillment_group (fulfillment_group_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_FULFILLMENT_GROUP_ITEM
--------------------------------------------------------

ALTER TABLE blc_fulfillment_group_item add constraint fkea74ebda5028dc55 foreign key (fulfillment_group_id)
  REFERENCES blc_fulfillment_group (fulfillment_group_id) ENABLE
/
ALTER TABLE blc_fulfillment_group_item add constraint fkea74ebda9af166df foreign key (order_item_id)
  REFERENCES blc_order_item (order_item_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_FULFILLMENT_OPTION_FIXED
--------------------------------------------------------

ALTER TABLE blc_fulfillment_option_fixed add constraint fk408360313e2fc4f9 foreign key (currency_code)
  REFERENCES blc_currency (currency_code) ENABLE
/
ALTER TABLE blc_fulfillment_option_fixed add constraint fk4083603181f34c7f foreign key (fulfillment_option_id)
  REFERENCES blc_fulfillment_option (fulfillment_option_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_FULFILLMENT_OPT_BANDED_PRC
--------------------------------------------------------

ALTER TABLE blc_fulfillment_opt_banded_prc add constraint fkb1fd71e981f34c7f foreign key (fulfillment_option_id)
  REFERENCES blc_fulfillment_option (fulfillment_option_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_FULFILLMENT_OPT_BANDED_WGT
--------------------------------------------------------

ALTER TABLE blc_fulfillment_opt_banded_wgt add constraint fkb1fd8aec81f34c7f foreign key (fulfillment_option_id)
  REFERENCES blc_fulfillment_option (fulfillment_option_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_FULFILLMENT_PRICE_BAND
--------------------------------------------------------

ALTER TABLE blc_fulfillment_price_band add constraint fk46c9ea726cdf59ca foreign key (fulfillment_option_id)
  REFERENCES blc_fulfillment_opt_banded_prc (fulfillment_option_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_FULFILLMENT_WEIGHT_BAND
--------------------------------------------------------

ALTER TABLE blc_fulfillment_weight_band add constraint fk6a048d95a0b429c3 foreign key (fulfillment_option_id)
  REFERENCES blc_fulfillment_opt_banded_wgt (fulfillment_option_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_GIFTWRAP_ORDER_ITEM
--------------------------------------------------------

ALTER TABLE blc_giftwrap_order_item add constraint fke1be1563b76b9466 foreign key (order_item_id)
  REFERENCES blc_discrete_order_item (order_item_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_IMG_STATIC_ASSET
--------------------------------------------------------

ALTER TABLE blc_img_static_asset add constraint fkcc4b772167f70b63 foreign key (static_asset_id)
  REFERENCES blc_static_asset (static_asset_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_ITEM_OFFER_QUALIFIER
--------------------------------------------------------

ALTER TABLE blc_item_offer_qualifier add constraint fkd9c50c619af166df foreign key (order_item_id)
  REFERENCES blc_order_item (order_item_id) ENABLE
/
ALTER TABLE blc_item_offer_qualifier add constraint fkd9c50c61d5f3faf4 foreign key (offer_id)
  REFERENCES blc_offer (offer_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_LOCALE
--------------------------------------------------------

ALTER TABLE blc_locale add constraint fk56c7dc203e2fc4f9 foreign key (currency_code)
  REFERENCES blc_currency (currency_code) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_OFFER_CODE
--------------------------------------------------------

ALTER TABLE blc_offer_code add constraint fk76b8c8d6d5f3faf4 foreign key (offer_id)
  REFERENCES blc_offer (offer_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_OFFER_INFO_FIELDS
--------------------------------------------------------

ALTER TABLE blc_offer_info_fields add constraint fka901886183ae7237 foreign key (offer_info_fields_id)
  REFERENCES blc_offer_info (offer_info_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_OFFER_RULE_MAP
--------------------------------------------------------

ALTER TABLE blc_offer_rule_map add constraint fkca468fe245c66d1d foreign key (blc_offer_offer_id)
  REFERENCES blc_offer (offer_id) ENABLE
/
ALTER TABLE blc_offer_rule_map add constraint fkca468fe2c11a218d foreign key (offer_rule_id)
  REFERENCES blc_offer_rule (offer_rule_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_ORDER
--------------------------------------------------------

ALTER TABLE blc_order add constraint fk8f5b64a83e2fc4f9 foreign key (currency_code)
  REFERENCES blc_currency (currency_code) ENABLE
/
ALTER TABLE blc_order add constraint fk8f5b64a87470f437 foreign key (customer_id)
  REFERENCES blc_customer (customer_id) ENABLE
/
ALTER TABLE blc_order add constraint fk8f5b64a8a1e1c128 foreign key (locale_code)
  REFERENCES blc_locale (locale_code) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_ORDER_ADJUSTMENT
--------------------------------------------------------

ALTER TABLE blc_order_adjustment add constraint fk1e92d16489fe8a02 foreign key (order_id)
  REFERENCES blc_order (order_id) ENABLE
/
ALTER TABLE blc_order_adjustment add constraint fk1e92d164d5f3faf4 foreign key (offer_id)
  REFERENCES blc_offer (offer_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_ORDER_ATTRIBUTE
--------------------------------------------------------

ALTER TABLE blc_order_attribute add constraint fkb3a467a589fe8a02 foreign key (order_id)
  REFERENCES blc_order (order_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_ORDER_ITEM
--------------------------------------------------------

ALTER TABLE blc_order_item add constraint fk9a2e704a15d1a13d foreign key (category_id)
  REFERENCES blc_category (category_id) ENABLE
/
ALTER TABLE blc_order_item add constraint fk9a2e704a77f565e1 foreign key (personal_message_id)
  REFERENCES blc_personal_message (personal_message_id) ENABLE
/
ALTER TABLE blc_order_item add constraint fk9a2e704a89fe8a02 foreign key (order_id)
  REFERENCES blc_order (order_id) ENABLE
/
ALTER TABLE blc_order_item add constraint fk9a2e704afd2f1f10 foreign key (gift_wrap_item_id)
  REFERENCES blc_giftwrap_order_item (order_item_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_ORDER_ITEM_ADD_ATTR
--------------------------------------------------------

ALTER TABLE blc_order_item_add_attr add constraint fka466ab44b76b9466 foreign key (order_item_id)
  REFERENCES blc_discrete_order_item (order_item_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_ORDER_ITEM_ADJUSTMENT
--------------------------------------------------------

ALTER TABLE blc_order_item_adjustment add constraint fka2658c829af166df foreign key (order_item_id)
  REFERENCES blc_order_item (order_item_id) ENABLE
/
ALTER TABLE blc_order_item_adjustment add constraint fka2658c82d5f3faf4 foreign key (offer_id)
  REFERENCES blc_offer (offer_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_ORDER_ITEM_ATTRIBUTE
--------------------------------------------------------

ALTER TABLE blc_order_item_attribute add constraint fk9f1ed0c79af166df foreign key (order_item_id)
  REFERENCES blc_order_item (order_item_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_ORDER_ITEM_DTL_ADJ
--------------------------------------------------------

ALTER TABLE blc_order_item_dtl_adj add constraint fk85f0248fd4aea2c0 foreign key (order_item_price_dtl_id)
  REFERENCES blc_order_item_price_dtl (order_item_price_dtl_id) ENABLE
/
ALTER TABLE blc_order_item_dtl_adj add constraint fk85f0248fd5f3faf4 foreign key (offer_id)
  REFERENCES blc_offer (offer_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_ORDER_ITEM_PRICE_DTL
--------------------------------------------------------

ALTER TABLE blc_order_item_price_dtl add constraint fk1fb64bf19af166df foreign key (order_item_id)
  REFERENCES blc_order_item (order_item_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_ORDER_MULTISHIP_OPTION
--------------------------------------------------------

ALTER TABLE blc_order_multiship_option add constraint fkb3d3f7d681f34c7f foreign key (fulfillment_option_id)
  REFERENCES blc_fulfillment_option (fulfillment_option_id) ENABLE
/
ALTER TABLE blc_order_multiship_option add constraint fkb3d3f7d689fe8a02 foreign key (order_id)
  REFERENCES blc_order (order_id) ENABLE
/
ALTER TABLE blc_order_multiship_option add constraint fkb3d3f7d69af166df foreign key (order_item_id)
  REFERENCES blc_order_item (order_item_id) ENABLE
/
ALTER TABLE blc_order_multiship_option add constraint fkb3d3f7d6c13085dd foreign key (address_id)
  REFERENCES blc_address (address_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_ORDER_OFFER_CODE_XREF
--------------------------------------------------------

ALTER TABLE blc_order_offer_code_xref add constraint fkfdf0e8533bb10f6d foreign key (offer_code_id)
  REFERENCES blc_offer_code (offer_code_id) ENABLE
/
ALTER TABLE blc_order_offer_code_xref add constraint fkfdf0e85389fe8a02 foreign key (order_id)
  REFERENCES blc_order (order_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_ORDER_PAYMENT
--------------------------------------------------------

ALTER TABLE blc_order_payment add constraint fk9517a14f89fe8a02 foreign key (order_id)
  REFERENCES blc_order (order_id) ENABLE
/
ALTER TABLE blc_order_payment add constraint fk9517a14fc13085dd foreign key (address_id)
  REFERENCES blc_address (address_id) ENABLE
/
ALTER TABLE blc_order_payment add constraint fk9517a14fca0b98e0 foreign key (customer_payment_id)
  REFERENCES blc_customer_payment (customer_payment_id) ENABLE
/
ALTER TABLE blc_order_payment add constraint fk9517a14fd894cb5d foreign key (phone_id)
  REFERENCES blc_phone (phone_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_ORDER_PAYMENT_DETAILS
--------------------------------------------------------

ALTER TABLE blc_order_payment_details add constraint fk6d3907323e2fc4f9 foreign key (currency_code)
  REFERENCES blc_currency (currency_code) ENABLE
/
ALTER TABLE blc_order_payment_details add constraint fk6d390732ce00a2eb foreign key (payment_info)
  REFERENCES blc_order_payment (payment_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_PAGE
--------------------------------------------------------

ALTER TABLE blc_page add constraint fkf41bedd5579fe59d foreign key (sandbox_id)
  REFERENCES blc_sandbox (sandbox_id) ENABLE
/
ALTER TABLE blc_page add constraint fkf41bedd5d49d3961 foreign key (page_tmplt_id)
  REFERENCES blc_page_tmplt (page_tmplt_id) ENABLE
/
ALTER TABLE blc_page add constraint fkf41bedd5f9c4a5b foreign key (orig_sandbox_id)
  REFERENCES blc_sandbox (sandbox_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_PAGE_FLD
--------------------------------------------------------

ALTER TABLE blc_page_fld add constraint fk86433ad4883c2667 foreign key (page_id)
  REFERENCES blc_page (page_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_PAGE_FLD_MAP
--------------------------------------------------------

ALTER TABLE blc_page_fld_map add constraint fke9ee09515aedd08a foreign key (page_fld_id)
  REFERENCES blc_page_fld (page_fld_id) ENABLE
/
ALTER TABLE blc_page_fld_map add constraint fke9ee0951883c2667 foreign key (page_id)
  REFERENCES blc_page (page_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_PAGE_RULE_MAP
--------------------------------------------------------

ALTER TABLE blc_page_rule_map add constraint fk1aba0ca336d91846 foreign key (page_rule_id)
  REFERENCES blc_page_rule (page_rule_id) ENABLE
/
ALTER TABLE blc_page_rule_map add constraint fk1aba0ca3c38455dd foreign key (blc_page_page_id)
  REFERENCES blc_page (page_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_PAGE_TMPLT
--------------------------------------------------------

ALTER TABLE blc_page_tmplt add constraint fk325c9d5a1e1c128 foreign key (locale_code)
  REFERENCES blc_locale (locale_code) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_PAYINFO_ADDITIONAL_FIELDS
--------------------------------------------------------

ALTER TABLE blc_payinfo_additional_fields add constraint fkf9378b824bc71d98 foreign key (payment_id)
  REFERENCES blc_order_payment (payment_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_PAYMENT_ADDITIONAL_FIELDS
--------------------------------------------------------

ALTER TABLE blc_payment_additional_fields add constraint fke3507032d956b1cc foreign key (payment_response_item_id)
  REFERENCES blc_payment_response_item (payment_response_item_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_PAYMENT_LOG
--------------------------------------------------------

ALTER TABLE blc_payment_log add constraint fka43703453e2fc4f9 foreign key (currency_code)
  REFERENCES blc_currency (currency_code) ENABLE
/
ALTER TABLE blc_payment_log add constraint fka43703457470f437 foreign key (customer_id)
  REFERENCES blc_customer (customer_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_PAYMENT_RESPONSE_ITEM
--------------------------------------------------------

ALTER TABLE blc_payment_response_item add constraint fk807b8f323e2fc4f9 foreign key (currency_code)
  REFERENCES blc_currency (currency_code) ENABLE
/
ALTER TABLE blc_payment_response_item add constraint fk807b8f327470f437 foreign key (customer_id)
  REFERENCES blc_customer (customer_id) ENABLE
/
ALTER TABLE blc_payment_response_item add constraint fk807b8f32be7dfc59 foreign key (payment_info_reference_number)
  REFERENCES blc_order_payment (reference_number) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_PGTMPLT_FLDGRP_XREF
--------------------------------------------------------

ALTER TABLE blc_pgtmplt_fldgrp_xref add constraint fk99d625f66a79bdb5 foreign key (fld_group_id)
  REFERENCES blc_fld_group (fld_group_id) ENABLE
/
ALTER TABLE blc_pgtmplt_fldgrp_xref add constraint fk99d625f6d49d3961 foreign key (page_tmplt_id)
  REFERENCES blc_page_tmplt (page_tmplt_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_PRODUCT
--------------------------------------------------------

ALTER TABLE blc_product add constraint fk5b95b7c9df057c3f foreign key (default_category_id)
  REFERENCES blc_category (category_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_PRODUCT_ATTRIBUTE
--------------------------------------------------------

ALTER TABLE blc_product_attribute add constraint fk56ce05865f11a0b7 foreign key (product_id)
  REFERENCES blc_product (product_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_PRODUCT_BUNDLE
--------------------------------------------------------

ALTER TABLE blc_product_bundle add constraint fk8cc5b85f11a0b7 foreign key (product_id)
  REFERENCES blc_product (product_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_PRODUCT_CROSS_SALE
--------------------------------------------------------

ALTER TABLE blc_product_cross_sale add constraint fk8324fb3c15d1a13d foreign key (category_id)
  REFERENCES blc_category (category_id) ENABLE
/
ALTER TABLE blc_product_cross_sale add constraint fk8324fb3c5f11a0b7 foreign key (product_id)
  REFERENCES blc_product (product_id) ENABLE
/
ALTER TABLE blc_product_cross_sale add constraint fk8324fb3c62d84f9b foreign key (related_sale_product_id)
  REFERENCES blc_product (product_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_PRODUCT_FEATURED
--------------------------------------------------------

ALTER TABLE blc_product_featured add constraint fk4c49ffe415d1a13d foreign key (category_id)
  REFERENCES blc_category (category_id) ENABLE
/
ALTER TABLE blc_product_featured add constraint fk4c49ffe45f11a0b7 foreign key (product_id)
  REFERENCES blc_product (product_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_PRODUCT_OPTION_VALUE
--------------------------------------------------------

ALTER TABLE blc_product_option_value add constraint fk6deeedbd92ea8136 foreign key (product_option_id)
  REFERENCES blc_product_option (product_option_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_PRODUCT_OPTION_XREF
--------------------------------------------------------

ALTER TABLE blc_product_option_xref add constraint fkda42ab2f5f11a0b7 foreign key (product_id)
  REFERENCES blc_product (product_id) ENABLE
/
ALTER TABLE blc_product_option_xref add constraint fkda42ab2f92ea8136 foreign key (product_option_id)
  REFERENCES blc_product_option (product_option_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_PRODUCT_SKU_XREF
--------------------------------------------------------

ALTER TABLE blc_product_sku_xref add constraint fkf2dbf6d35f11a0b7 foreign key (product_id)
  REFERENCES blc_product (product_id) ENABLE
/
ALTER TABLE blc_product_sku_xref add constraint fkf2dbf6d3b78c9977 foreign key (sku_id)
  REFERENCES blc_sku (sku_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_PRODUCT_UP_SALE
--------------------------------------------------------

ALTER TABLE blc_product_up_sale add constraint fkf69054f515d1a13d foreign key (category_id)
  REFERENCES blc_category (category_id) ENABLE
/
ALTER TABLE blc_product_up_sale add constraint fkf69054f55f11a0b7 foreign key (product_id)
  REFERENCES blc_product (product_id) ENABLE
/
ALTER TABLE blc_product_up_sale add constraint fkf69054f562d84f9b foreign key (related_sale_product_id)
  REFERENCES blc_product (product_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_QUAL_CRIT_OFFER_XREF
--------------------------------------------------------

ALTER TABLE blc_qual_crit_offer_xref add constraint fkd592e9193615a91a foreign key (offer_item_criteria_id)
  REFERENCES blc_offer_item_criteria (offer_item_criteria_id) ENABLE
/
ALTER TABLE blc_qual_crit_offer_xref add constraint fkd592e919d5f3faf4 foreign key (offer_id)
  REFERENCES blc_offer (offer_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_QUAL_CRIT_PAGE_XREF
--------------------------------------------------------

ALTER TABLE blc_qual_crit_page_xref add constraint fk874be590378418cd foreign key (page_item_criteria_id)
  REFERENCES blc_page_item_criteria (page_item_criteria_id) ENABLE
/
ALTER TABLE blc_qual_crit_page_xref add constraint fk874be590883c2667 foreign key (page_id)
  REFERENCES blc_page (page_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_QUAL_CRIT_SC_XREF
--------------------------------------------------------

ALTER TABLE blc_qual_crit_sc_xref add constraint fkc4a353af13d95585 foreign key (sc_id)
  REFERENCES blc_sc (sc_id) ENABLE
/
ALTER TABLE blc_qual_crit_sc_xref add constraint fkc4a353af85c77f2b foreign key (sc_item_criteria_id)
  REFERENCES blc_sc_item_criteria (sc_item_criteria_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_RATING_DETAIL
--------------------------------------------------------

ALTER TABLE blc_rating_detail add constraint fkc9d04ad7470f437 foreign key (customer_id)
  REFERENCES blc_customer (customer_id) ENABLE
/
ALTER TABLE blc_rating_detail add constraint fkc9d04add4e76bf4 foreign key (rating_summary_id)
  REFERENCES blc_rating_summary (rating_summary_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_REVIEW_DETAIL
--------------------------------------------------------

ALTER TABLE blc_review_detail add constraint fk9cd7e69245dc39e0 foreign key (rating_detail_id)
  REFERENCES blc_rating_detail (rating_detail_id) ENABLE
/
ALTER TABLE blc_review_detail add constraint fk9cd7e6927470f437 foreign key (customer_id)
  REFERENCES blc_customer (customer_id) ENABLE
/
ALTER TABLE blc_review_detail add constraint fk9cd7e692d4e76bf4 foreign key (rating_summary_id)
  REFERENCES blc_rating_summary (rating_summary_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_REVIEW_FEEDBACK
--------------------------------------------------------

ALTER TABLE blc_review_feedback add constraint fk7cc929867470f437 foreign key (customer_id)
  REFERENCES blc_customer (customer_id) ENABLE
/
ALTER TABLE blc_review_feedback add constraint fk7cc92986ae4769d6 foreign key (review_detail_id)
  REFERENCES blc_review_detail (review_detail_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_SC
--------------------------------------------------------

ALTER TABLE blc_sc add constraint fk74eeb716579fe59d foreign key (sandbox_id)
  REFERENCES blc_sandbox (sandbox_id) ENABLE
/
ALTER TABLE blc_sc add constraint fk74eeb71671ebfa46 foreign key (sc_type_id)
  REFERENCES blc_sc_type (sc_type_id) ENABLE
/
ALTER TABLE blc_sc add constraint fk74eeb716a1e1c128 foreign key (locale_code)
  REFERENCES blc_locale (locale_code) ENABLE
/
ALTER TABLE blc_sc add constraint fk74eeb716f9c4a5b foreign key (orig_sandbox_id)
  REFERENCES blc_sandbox (sandbox_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_SC_FLD
--------------------------------------------------------

ALTER TABLE blc_sc_fld add constraint fk621d7b9513d95585 foreign key (sc_id)
  REFERENCES blc_sc (sc_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_SC_FLDGRP_XREF
--------------------------------------------------------

ALTER TABLE blc_sc_fldgrp_xref add constraint fk71612aea6a79bdb5 foreign key (fld_group_id)
  REFERENCES blc_fld_group (fld_group_id) ENABLE
/
ALTER TABLE blc_sc_fldgrp_xref add constraint fk71612aeaf6b0ba84 foreign key (sc_fld_tmplt_id)
  REFERENCES blc_sc_fld_tmplt (sc_fld_tmplt_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_SC_FLD_MAP
--------------------------------------------------------

ALTER TABLE blc_sc_fld_map add constraint fkd948019213d95585 foreign key (sc_id)
  REFERENCES blc_sc (sc_id) ENABLE
/
ALTER TABLE blc_sc_fld_map add constraint fkd9480192dd6fd28a foreign key (sc_fld_id)
  REFERENCES blc_sc_fld (sc_fld_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_SC_RULE_MAP
--------------------------------------------------------

ALTER TABLE blc_sc_rule_map add constraint fk169f1c82156e72fc foreign key (blc_sc_sc_id)
  REFERENCES blc_sc (sc_id) ENABLE
/
ALTER TABLE blc_sc_rule_map add constraint fk169f1c8256e51a06 foreign key (sc_rule_id)
  REFERENCES blc_sc_rule (sc_rule_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_SC_TYPE
--------------------------------------------------------

ALTER TABLE blc_sc_type add constraint fke19886c3f6b0ba84 foreign key (sc_fld_tmplt_id)
  REFERENCES blc_sc_fld_tmplt (sc_fld_tmplt_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_SEARCH_FACET
--------------------------------------------------------

ALTER TABLE blc_search_facet add constraint fk4ffcc9863c3907c4 foreign key (field_id)
  REFERENCES blc_field (field_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_SEARCH_FACET_RANGE
--------------------------------------------------------

ALTER TABLE blc_search_facet_range add constraint fk7ec3b124b96b1c93 foreign key (search_facet_id)
  REFERENCES blc_search_facet (search_facet_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_SEARCH_FACET_XREF
--------------------------------------------------------

ALTER TABLE blc_search_facet_xref add constraint fk35a63034b96b1c93 foreign key (search_facet_id)
  REFERENCES blc_search_facet (search_facet_id) ENABLE
/
ALTER TABLE blc_search_facet_xref add constraint fk35a63034da7e1c7c foreign key (required_facet_id)
  REFERENCES blc_search_facet (search_facet_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_SITE
--------------------------------------------------------

ALTER TABLE blc_site add constraint fkf41d6a8d64ebcce3 foreign key (production_sandbox_id)
  REFERENCES blc_sandbox (sandbox_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_SITE_CATALOG
--------------------------------------------------------

ALTER TABLE blc_site_catalog add constraint fk5f3f2047843a8b63 foreign key (site_id)
  REFERENCES blc_site (site_id) ENABLE
/
ALTER TABLE blc_site_catalog add constraint fk5f3f2047a350c7f1 foreign key (catalog_id)
  REFERENCES blc_catalog (catalog_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_SITE_SANDBOX
--------------------------------------------------------

ALTER TABLE blc_site_sandbox add constraint fkad4f7ef5579fe59d foreign key (sandbox_id)
  REFERENCES blc_sandbox (sandbox_id) ENABLE
/
ALTER TABLE blc_site_sandbox add constraint fkad4f7ef5843a8b63 foreign key (site_id)
  REFERENCES blc_site (site_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_SKU
--------------------------------------------------------

ALTER TABLE blc_sku add constraint fk28e82cf73e2fc4f9 foreign key (currency_code)
  REFERENCES blc_currency (currency_code) ENABLE
/
ALTER TABLE blc_sku add constraint fk28e82cf77e555d75 foreign key (default_product_id)
  REFERENCES blc_product (product_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_SKU_ATTRIBUTE
--------------------------------------------------------

ALTER TABLE blc_sku_attribute add constraint fk6c6a5934b78c9977 foreign key (sku_id)
  REFERENCES blc_sku (sku_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_SKU_BUNDLE_ITEM
--------------------------------------------------------

ALTER TABLE blc_sku_bundle_item add constraint fkd55968b78c9977 foreign key (sku_id)
  REFERENCES blc_sku (sku_id) ENABLE
/
ALTER TABLE blc_sku_bundle_item add constraint fkd55968ccf29b96 foreign key (product_bundle_id)
  REFERENCES blc_product_bundle (product_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_SKU_FEE
--------------------------------------------------------

ALTER TABLE blc_sku_fee add constraint fkeeb7181e3e2fc4f9 foreign key (currency_code)
  REFERENCES blc_currency (currency_code) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_SKU_FEE_XREF
--------------------------------------------------------

ALTER TABLE blc_sku_fee_xref add constraint fkd88d409cb78c9977 foreign key (sku_id)
  REFERENCES blc_sku (sku_id) ENABLE
/
ALTER TABLE blc_sku_fee_xref add constraint fkd88d409ccf4c9a82 foreign key (sku_fee_id)
  REFERENCES blc_sku_fee (sku_fee_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_SKU_FULFILLMENT_EXCLUDED
--------------------------------------------------------

ALTER TABLE blc_sku_fulfillment_excluded add constraint fk84162d7381f34c7f foreign key (fulfillment_option_id)
  REFERENCES blc_fulfillment_option (fulfillment_option_id) ENABLE
/
ALTER TABLE blc_sku_fulfillment_excluded add constraint fk84162d73b78c9977 foreign key (sku_id)
  REFERENCES blc_sku (sku_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_SKU_FULFILLMENT_FLAT_RATES
--------------------------------------------------------

ALTER TABLE blc_sku_fulfillment_flat_rates add constraint fkc1988c9681f34c7f foreign key (fulfillment_option_id)
  REFERENCES blc_fulfillment_option (fulfillment_option_id) ENABLE
/
ALTER TABLE blc_sku_fulfillment_flat_rates add constraint fkc1988c96b78c9977 foreign key (sku_id)
  REFERENCES blc_sku (sku_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_SKU_MEDIA_MAP
--------------------------------------------------------

ALTER TABLE blc_sku_media_map add constraint fkeb4aecf96e4720e0 foreign key (media_id)
  REFERENCES blc_media (media_id) ENABLE
/
ALTER TABLE blc_sku_media_map add constraint fkeb4aecf9d93d857f foreign key (blc_sku_sku_id)
  REFERENCES blc_sku (sku_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_SKU_OPTION_VALUE_XREF
--------------------------------------------------------

ALTER TABLE blc_sku_option_value_xref add constraint fk7b61dc0bb0c16a73 foreign key (product_option_value_id)
  REFERENCES blc_product_option_value (product_option_value_id) ENABLE
/
ALTER TABLE blc_sku_option_value_xref add constraint fk7b61dc0bb78c9977 foreign key (sku_id)
  REFERENCES blc_sku (sku_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_STATE
--------------------------------------------------------

ALTER TABLE blc_state add constraint fk8f94a1eba46e16cf foreign key (country)
  REFERENCES blc_country (abbreviation) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_STATIC_ASSET
--------------------------------------------------------

ALTER TABLE blc_static_asset add constraint fk9875fb05579fe59d foreign key (sandbox_id)
  REFERENCES blc_sandbox (sandbox_id) ENABLE
/
ALTER TABLE blc_static_asset add constraint fk9875fb05f9c4a5b foreign key (orig_sandbox_id)
  REFERENCES blc_sandbox (sandbox_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_TAR_CRIT_OFFER_XREF
--------------------------------------------------------

ALTER TABLE blc_tar_crit_offer_xref add constraint fk125f58033615a91a foreign key (offer_item_criteria_id)
  REFERENCES blc_offer_item_criteria (offer_item_criteria_id) ENABLE
/
ALTER TABLE blc_tar_crit_offer_xref add constraint fk125f5803d5f3faf4 foreign key (offer_id)
  REFERENCES blc_offer (offer_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table BLC_TAX_DETAIL
--------------------------------------------------------

ALTER TABLE blc_tax_detail add constraint fkeabe4a4b3e2fc4f9 foreign key (currency_code)
  REFERENCES blc_currency (currency_code) ENABLE
/
ALTER TABLE blc_tax_detail add constraint fkeabe4a4bc50d449 foreign key (module_config_id)
  REFERENCES blc_module_configuration (module_config_id) ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table SANDBOX_ITEM_ACTION
--------------------------------------------------------

ALTER TABLE sandbox_item_action add constraint fkb270d74a9797a024 foreign key (sandbox_action_id)
  REFERENCES blc_sandbox_action (sandbox_action_id) ENABLE
/
ALTER TABLE sandbox_item_action add constraint fkb270d74afe239304 foreign key (sandbox_item_id)
  REFERENCES blc_sandbox_item (sandbox_item_id) ENABLE
/
