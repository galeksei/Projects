; Some utility functions that you may find useful.
(define (apply-to-all proc items)
  (if (null? items)
      '()
      (cons (proc (car items))
            (apply-to-all proc (cdr items)))))

(define (keep-if predicate sequence)
  (cond ((null? sequence) nil)
        ((predicate (car sequence))
         (cons (car sequence)
               (keep-if predicate (cdr sequence))))
        (else (keep-if predicate (cdr sequence)))))

(define (accumulate op initial sequence)
  (if (null? sequence)
      initial
      (op (car sequence)
          (accumulate op initial (cdr sequence)))))

(define (caar x) (car (car x)))
(define (cadr x) (car (cdr x)))
(define (cddr x) (cdr (cdr x)))
(define (cadar x) (car (cdr (car x))))

; Problem 18
;; Turns a list of pairs into a pair of lists
(define (zip pairs)
  (define (helper1 pairs)
    (cond
      ((null? pairs) nil)
      (else (cons (caar pairs) (helper1 (cdr pairs))))
      )
    )
  (define (helper2 pairs)
    (cond
      ((null? pairs) nil)
      (else (cons (cadr (car pairs)) (helper2 (cdr pairs))))
      )
    )
  (list (helper1 pairs) (helper2 pairs))
  )

(zip '())
; expect (() ())
(zip '((1 2)))
; expect ((1) (2))
(zip '((1 2) (3 4) (5 6)))
; expect ((1 3 5) (2 4 6))

; Problem 19

;; A list of all ways to partition TOTAL, where  each partition must
;; be at most MAX-VALUE and there are at most MAX-PIECES partitions.
(define (list-partitions total max-pieces max-value)
  (define (partitions total max-pieces max-value)
    (cond 
      ((= total 0) (list (list 0)))
      ((< total 0) (list nil))
      ((< max-value 1) (list nil))
      ((= max-pieces 0) (list nil))
      (else (begin (define lst1 (partitions (- total max-value) (- max-pieces 1) max-value)) 
                    (define lst2 (partitions total max-pieces (- max-value 1))) 
                       (append (combiner max-value lst1) lst2)))
      )
    )
  (define (combiner value lst)
    (cond
      ((null? lst) nil)
      ((null? (car lst)) (combiner value (cdr lst)))
      (else  (cons (append (list value) (car lst)) (combiner value (cdr lst))))
      )
    )
  (define (DESTROYER! lst)
    (cond
      ((null? lst) nil)
      ((null? (car lst)) nil)
      ((list? (car lst)) (cons (DESTROYER! (car lst)) (DESTROYER! (cdr lst))))
      ((= (car lst) 0) nil)
      (else (cons (car lst) (DESTROYER! (cdr lst))))
      )
    )
  (DESTROYER! (partitions total max-pieces max-value))
  )

(list-partitions 5 2 4)
; expect ((4 1) (3 2))
(list-partitions 7 3 5)
; expect ((5 2) (5 1 1) (4 3) (4 2 1) (3 3 1) (3 2 2))


; Problem 20
;; Returns a function that takes in an expression and checks if it is the special
;; form FORM
(define (check-special form)
  (lambda (expr) (equal? form (car expr))))

(define lambda? (check-special 'lambda))
(define define? (check-special 'define))
(define quoted? (check-special 'quote))
(define let?    (check-special 'let))

;; Converts all let special forms in EXPR into equivalent forms using lambda
; (define (analyze expr)
;    (cond ((atom? expr)
;    expr
;    )
;    ((quoted? expr)
;    expr
;    )
;   ((or (lambda? expr)
;    (define? expr))
;    (let ((form (car expr))
;    (params (cadr expr))
;    (body (cddr expr)))
   ; (cond
   ; ((null? body) nil)
   ; (else (append (list form params) (apply-to-all analyze body)))
;    )
;   ;(list form params (analyze (car body)))
;    ))
;    ((let? expr)
;    (let ((values (cadr expr))
;    (body (cddr expr)))
;    (list (list 'lambda body (car (car values))) (car (cdr (car values))))
;    ))
;    (else
;    expr
;    )))
(define (analyze expr)
  (cond ((atom? expr)
         expr
         )
        ((quoted? expr)
         expr
         )
        ((or (lambda? expr)
             (define? expr))
         (let ((form   (car expr))
               (params (cadr expr))
               (body   (cddr expr)))
              (cond
   ((null? body) nil)
   (else (append (list form params) (apply-to-all analyze body)))
           )
           ))
        ((let? expr)
         (let ((values (cadr expr))
               (body   (cddr expr)))
           (define zipped (zip values))
           (cond 
            ((null? body) nil)
           (else 
           (append 
          (list (append (list 'lambda (car zipped))  (apply-to-all analyze body) )) 
          (apply-to-all analyze (cadr zipped))
           )))))
        (else
         expr
         )))
(analyze 1)
; expect 1
(analyze 'a)
; expect a
(analyze '(+ 1 2))
; expect (+ 1 2)

;; Quoted expressions remain the same
(analyze '(quote (let ((a 1) (b 2)) (+ a b))))
; expect (quote (let ((a 1) (b 2)) (+ a b)))

;; Lambda parameters not affected, but body affected
(analyze '(lambda (let a b) (+ let a b)))
; expect (lambda (let a b) (+ let a b))
(analyze '(lambda (x) a (let ((a x)) a)))
; expect (lambda (x) a ((lambda (a) a) x))

(analyze '(let ((a 1)
                (b 2))
            (+ a b)))
; expect ((lambda (a b) (+ a b)) 1 2)
(analyze '(let ((a (let ((a 2)) a))
                (b 2))
            (+ a b)))
; expect ((lambda (a b) (+ a b)) ((lambda (a) a) 2) 2)
(analyze '(let ((a 1))
            (let ((b a))
              b)))
; expect ((lambda (a) ((lambda (b) b) a)) 1)
(analyze '(let ((x 4) (y (+ 2 3))) (+ x y)))
; expect ((lambda (x y) (+ x y)) 4 (+ 2 3))



;; Problem 21 (optional)
;; Draw the hax image using turtle graphics.
(define (hax d k)
  'YOUR-CODE-HERE
  nil)




