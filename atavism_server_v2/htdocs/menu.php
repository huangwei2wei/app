<?php
include './includes/menu.class.php';
include './includes/tidy_menu.class.php';

// Build out menu structure, the syntax makes it easy to see the levels  
$menu = menu::factory()  
            ->add('Home', 'index.php?middle=Home', menu::factory())
			->add('Administration', '', menu::factory()  
                ->add('Server Task', 'index.php?middle=ServerTasks')  
                ->add('Users', 'index.php?middle=Users')
				->add('Backdating', 'index.php?middle=Backdating'))  
            ->add('Database', '', menu::factory()  
                ->add('Items', 'index.php?middle=DBItems')  
                ->add('Quests', 'index.php?middle=DBQuest', menu::factory()))
			->add('Atavism Forum', 'http://atavismonline.com/forum', menu::factory());  

  
// (optional) Add some attributes to the main list  
$menu->attrs = array  
(  
    'id'    => 'navigation',  
    'class' => 'menu',  
);  
/* 
You can also do the following instead 
$menu->id = 'navigation'; 
$menu->class = 'menu'; 
*/  
  
// (optional) Tell it the current active item  
$menu->current = 'level-three.php';  
  
/* 
Echo the menu 
Normally, you could just do this: 
echo $menu; 
But for this example, we're using the tidy_menu class to output nice, readable HTML, 
whereas just echoing normally would render the HTML in a single line. 
*/  
echo new tidy_menu($menu);  
?>