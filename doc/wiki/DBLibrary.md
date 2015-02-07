#DBLibrary Summary
in android orm framework. Make use of sqlite handle. one line just to add/delete/update/query. holder one-more,more-one entity

```java
	// data file
	KJDB db = KJDB.create(this);
	User ugc = new User(); //warn: The ugc must have id field or @ID annotate
	ugc.setEmail("kymjs123@gmail.com");
	ugc.setName("kymjs");
	db.save(ugc);
```

```java
	//one - many
	public class Parent{  //JavaBean
		private int id;
		@OneToMany(manyColumn = "parentId")
		private OneToManyLazyLoader<Parent ,Child> children;
		/*....*/
	}
	
	public class Child{ //JavaBean
		private int id;
		private String text;
		@ManyToOne(column = "parentId")
		private  Parent  parent;
		/*....*/
	}
	
	List<Parent> all = db.findAll(Parent.class);
			for( Parent  item : all){
				if(item.getChildren ().getList().size()>0)
					Toast.makeText(this,item.getText() + item.getChildren().getList().get(0).getText(),Toast.LENGTH_LONG).show();
			}

```