package {app.id};

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet Filter implementation class IndexFilter
 */
@WebFilter(urlPatterns = IndexFilter.PATH)
public class IndexFilter implements Filter {

	public static final String PATH = "/webapp/index.html";
	
    /**
     * Default constructor. 
     */
    public IndexFilter() {
    }

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        String uri = request.getContextPath() + PATH;

        if (!request.getRequestURI().equals(uri)) {
            response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY); // 301
            response.setHeader("Location", uri);
            response.setHeader("Connection", "close");
        } else {
            chain.doFilter(req, res);
        }
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
	}

}
