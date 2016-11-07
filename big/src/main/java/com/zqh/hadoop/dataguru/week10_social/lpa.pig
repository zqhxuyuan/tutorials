-- Dataguru Hadoop Course
-- Code by James 


%default NODE /user/huangjun/dataguru/node
%default EDGE /user/huangjun/dataguru/wiki-Vote
%default OUTPUT /user/huangjun/dataguru/node_1


-- Load Data
node = LOAD '$NODE' AS ( id, label );
edge = LOAD '$EDGE' AS ( source, target );

-- Get label
label_jnd = JOIN edge BY source, node BY id USING 'REPLICATED'; -- If node table is cache-able

label_final = FOREACH label_jnd GENERATE edge::target AS user, edge::source AS source, node::label AS label;

-- Propagation
prop_cnt = FOREACH ( GROUP label_final BY (user, label) )
    GENERATE FLATTEN(group) AS (user, label), COUNT(label_final) AS cnt;

prop_grp = FOREACH ( GROUP prop_cnt BY user ) 
{
    labels_srt = ORDER prop_cnt BY cnt DESC;
    labels_top = LIMIT labels_srt 1;
    GENERATE FLATTEN(labels_top);
}

prop = FOREACH prop_grp GENERATE user AS user, label AS new_label;

-- Check stop criterion
check_jnd = JOIN node BY id LEFT OUTER, prop BY user;

check_prj = FOREACH check_jnd 
    GENERATE node::id AS id, label AS old_label, ( new_label IS NULL ? label:new_label ) AS new_label;

check_flt = FILTER check_prj BY NOT old_label MATCHES new_label;

check = FOREACH ( GROUP check_flt ALL )
{
    labels_prj = check_flt.new_label;
    labels_dst = DISTINCT labels_prj;
    GENERATE COUNT(check_flt), COUNT(labels_dst);
}

-- Output
out = FOREACH check_prj GENERATE id, new_label AS label;

STORE out INTO '$OUTPUT';

DUMP check;


