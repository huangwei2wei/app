#!/usr/bin/python

import re
import sys

unhandled_keys = {}

def patch_oid(matchobj):
    prop_str = matchobj.group(1)
    prop_name_str = matchobj.group(2)
    whitespace_str = matchobj.group(3)
    value_str = matchobj.group(4)
    if prop_name_str == 'oid':
        pass
    elif prop_name_str == 'objectOID':
        pass
    elif prop_name_str == 'persistence_id':
        pass
    elif prop_name_str == ':instance':
        pass
    elif prop_name_str == 'instanceOid':
        pass
    elif prop_name_str == 'ownerOid':
        pass
    elif prop_name_str == 'playerOid':
        pass
    elif prop_name_str == 'objRef':
        pass
    elif prop_name_str == 'lastInterp':
        # this is supposed to be a long, so leave it alone
        return matchobj.group(0)
    else:
        unhandled_keys[prop_str] = prop_str
        return matchobj.group(0)
    
    return prop_str + whitespace_str + '<object class=\"multiverse.server.engine.OID\"> \\n' + whitespace_str + ' ' + '<void property=\"data\"> \\n' + whitespace_str + '  ' + value_str + ' \\n' + whitespace_str + ' ' +   '</void> \\n' + whitespace_str + '</object>'
    
def update_line(line):
    str_pattern = r'(<void property=\\"([^"]*)\\"\s*> \\n)(\s+)(<long>[0-9]+</long>)'
    oid_pattern = re.compile(str_pattern)
    return re.sub(oid_pattern, patch_oid, line)

def update_line2(line):
    str_pattern = r'(<void method=\\"put\\"\s*> \\n\s+<string>([^<]*)</string> \\n)(\s+)(<long>[0-9]+</long>)'
    oid_pattern = re.compile(str_pattern)
    return re.sub(oid_pattern, patch_oid, line)

# file_in = open('warbase.sql')
# file_out = open('warbase2.sql', 'w')

file_in = sys.stdin
file_out = sys.stdout

while (True):
    line = file_in.readline()
    if not line:
        break
    line2 = update_line(line)
    rv = update_line2(line2)
    file_out.write(rv)
file_out.close()
file_in.close()

for key in unhandled_keys.keys():
    print key
