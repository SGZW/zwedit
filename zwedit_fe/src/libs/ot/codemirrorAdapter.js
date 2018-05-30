import TextOperation from '@/libs/ot/textOperation';

class CodemirrorAdapter {
   
    static cmpPos (a, b) {
        if (a.line < b.line) { return -1; }
        if (a.line > b.line) { return 1; }
        if (a.ch < b.ch)     { return -1; }
        if (a.ch > b.ch)     { return 1; }
        return 0;
    }
    
    static posLe (a, b) { return this.cmpPos(a, b) <= 0; } 
    
    static codemirrorDocLength (doc) {
        return doc.indexFromPos({ line: doc.lastLine(), ch: 0 }) +
            doc.getLine(doc.lastLine()).length;
    }

    static operationFromCodeMirrorChanges (changes, doc) {
        var docEndLength = this.codemirrorDocLength(doc);
        var operation    = new TextOperation().retain(docEndLength);

        var indexFromPos = function (pos) {
          return doc.indexFromPos(pos);
        };

        function last (arr) { return arr[arr.length - 1]; }

        function sumLengths (strArr) {
          if (strArr.length === 0) { return 0; }
          var sum = 0;
          for (var i = 0; i < strArr.length; i++) { sum += strArr[i].length; }
          return sum + strArr.length - 1;
        }
        
        var that = this;
        function updateIndexFromPos (indexFromPos, change) {
          return function (pos) {
            if (that.posLe(pos, change.from)) { return indexFromPos(pos); }
            if (that.posLe(change.to, pos)) {
              return indexFromPos({
                line: pos.line + change.text.length - 1 - (change.to.line - change.from.line),
                ch: (change.to.line < pos.line) ?
                  pos.ch :
                  (change.text.length <= 1) ?
                    pos.ch - (change.to.ch - change.from.ch) + sumLengths(change.text) :
                    pos.ch - change.to.ch + last(change.text).length
              }) + sumLengths(change.removed) - sumLengths(change.text);
            }
            if (change.from.line === pos.line) {
              return indexFromPos(change.from) + pos.ch - change.from.ch;
            }
            return indexFromPos(change.from) +
              sumLengths(change.removed.slice(0, pos.line - change.from.line)) +
              1 + pos.ch;
          };
        }

        for (var i = changes.length - 1; i >= 0; i--) {
          var change = changes[i];
          indexFromPos = updateIndexFromPos(indexFromPos, change);

          var fromIndex = indexFromPos(change.from);
          var restLength = docEndLength - fromIndex - sumLengths(change.text);

          operation = new TextOperation()
            .retain(fromIndex)
            .delete(sumLengths(change.removed))
            .insert(change.text.join('\n'))
            .retain(restLength)
            .compose(operation);

          docEndLength += sumLengths(change.removed) - sumLengths(change.text);
        }
        return operation;
    }
    
    static applyOperationToCodeMirror (cm, operation) {
        cm.operation(function () {
          var ops = operation.ops;
          var index = 0; // holds the current index into CodeMirror's content
          for (var i = 0, l = ops.length; i < l; i++) {
            var op = ops[i];
            if (TextOperation.isRetain(op)) {
              index += op;
            } else if (TextOperation.isInsert(op)) {
              cm.replaceRange(op, cm.posFromIndex(index));
              index += op.length;
            } else if (TextOperation.isDelete(op)) {
              var from = cm.posFromIndex(index);
              var to   = cm.posFromIndex(index - op);
              cm.replaceRange('', from, to);
            }
          }
        });
    }
    
    static getValue(cm) {
        return cm.getValue();
    }
    
    static setValue(cm, str) {
        cm.setValue(str);
    }
}

export default CodemirrorAdapter;
