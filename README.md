## How to add knowledge base and proofs:
<strong>Open the provided <code>kb.txt</code> file view example.</strong>
<ul>
<li>Add all knowledge base sentence after "Knowledge Base:"</li>
<li>Each new sentence should have an empty line above it and below it.</li>
<li>Add all proof sentence after "Prove the following by refutation:"</li>
<li>Each new proof sentence mush have a empty line above it and below it, exception being the
last sentence.</li>
</ul>

#### Syntax for sentences:
<ul>
<li>Negation: ~</li>
<li>And: &&</li>
<li>Or: ||</li>
<li>Implies: =></li>
<li>Double implies: <=></li>
</ul>
To apply negation, <strong>make sure to not add a space</strong> after the symbol. <br>
<b>Example:</b><br>
<code>
~( A || B )
</code><br>
<code>
~( P || ~R ) => S
</code><br>
All other literals and symbols should have a space before and after each.

## How to execute program:
After adding all the knowledge base sentences and proof sentence, rick click on test
and select "Run Test.Main()".<br>
Output is in console.
