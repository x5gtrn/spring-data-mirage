/*
 * Copyright 2011 Daisuke Miyamoto.
 * Created on 2012/12/09
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.springframework.data.mirage.repository.example;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.List;

import com.google.common.collect.Iterables;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * TODO for daisuke
 * 
 * @since 1.0
 * @version $Id$
 * @author daisuke
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/test-context.xml")
@Transactional
@SuppressWarnings("javadoc")
public class EntityRepositoryTest {
	
	@Autowired
	EntityRepository repos;
	
	
	@Test
	public void test() {
		assertThat(repos, is(notNullValue()));
		assertThat(repos.count(), is(0L));
		Entity foo = repos.save(new Entity("foo"));
		assertThat(repos.count(), is(1L));
		Entity bar = repos.save(new Entity("bar"));
		assertThat(repos.count(), is(2L));
		repos.save(new Entity("foo"));
		repos.save(new Entity("foo2"));
		repos.save(new Entity("foo3"));
		repos.save(new Entity("bar2"));
		repos.save(new Entity("bar3"));
		
		Entity foundFoo = repos.findOne(foo.getId());
		assertThat(foundFoo.getId(), is(foo.getId()));
		assertThat(foundFoo.getStr(), is("foo"));
		
		Entity foundBar = repos.findOne(bar.getId());
		assertThat(foundBar.getId(), is(bar.getId()));
		assertThat(foundBar.getStr(), is("bar"));
		
		Iterable<Entity> all = repos.findAll();
		assertThat(Iterables.size(all), is(7));
		
		Page<Entity> page1 = repos.findAll(new PageRequest(1/*zero based*/, 2, Direction.ASC, "str"));
		assertThat(page1.getNumber(), is(1));
		assertThat(page1.getNumberOfElements(), is(2));
		assertThat(page1.getTotalElements(), is(7L));
		assertThat(page1.getContent().size(), is(2));
		assertThat(page1.getContent().get(0).getStr(), is("bar3"));
		assertThat(page1.getContent().get(1).getStr(), is("foo"));
		
		Page<Entity> page2 = repos.findAll(new PageRequest(2/*zero based*/, 2, Direction.ASC, "str"));
		assertThat(page2.getNumber(), is(2));
		assertThat(page2.getNumberOfElements(), is(2));
		assertThat(page2.getTotalElements(), is(7L));
		assertThat(page2.getContent().size(), is(2));
		assertThat(page2.getContent().get(0).getStr(), is("foo"));
		assertThat(page2.getContent().get(1).getStr(), is("foo2"));
		
		List<Entity> foundFoos = repos.findByStr("foo");
		assertThat(foundFoos.size(), is(2));
		
		List<Entity> foundStartsWithFoos = repos.findByStrStartsWith("foo");
		assertThat(foundStartsWithFoos.size(), is(4));
		
		List<Entity> foundBars = repos.findByStr("bar");
		assertThat(foundBars.size(), is(1));
		
		List<Entity> foundStartsWithBars = repos.findByStrStartsWith("bar");
		assertThat(foundStartsWithBars.size(), is(3));
		
		List<Entity> foundQux = repos.findByStr("qux");
		assertThat(foundQux.size(), is(0));
	}
}
